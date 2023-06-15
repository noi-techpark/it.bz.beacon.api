// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.service.issue;

import com.google.common.collect.Lists;
import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.model.*;
import it.bz.beacon.api.db.repository.IssueRepository;
import it.bz.beacon.api.exception.NotImplementedException;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.db.IssueNotFoundException;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.service.beacon.IBeaconDataService;
import it.bz.beacon.api.service.beacon.IBeaconService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class IssueService implements IIssueService {

    @Autowired
    private IssueRepository repository;

    @Autowired
    private IBeaconService beaconService;

    @Autowired
    private IBeaconDataService beaconDataService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private IIssueCommentService issueCommentService;

    final static Logger log = LoggerFactory.getLogger(IssueService.class);

    @Override
    @Transactional
    public List<BeaconIssue> findAll(boolean onlyUnresolved) {
        List<Issue> issues = onlyUnresolved ? repository.findAllByResolvedIsFalse() : repository.findAll();

        return mapIssuesToBeaconIssues(issues);
    }

    @Override
    @Transactional
    public List<BeaconIssue> findAllByBeacon(BeaconData beaconData, boolean onlyUnresolved) {
        List<Issue> issues = onlyUnresolved ? repository.findAllByBeaconDataAndSolution(beaconData, null) : repository.findAllByBeaconData(beaconData);

        return mapIssuesToBeaconIssues(issues);
    }

    @Override
    @Transactional
    public BeaconIssue find(long id) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);
        Beacon beacon = beaconService.find(issue.getBeaconData().getId());
        IssueComment issueComment = null;

        if (issue.isResolved())
            issueComment = issueCommentService.findLastCommentByIssue(issue);

        return BeaconIssue.fromIssue(issue, beacon, issueComment);
    }

    @Override
    @Transactional
    public BeaconIssue create(IssueCreation issueCreation) {
        BeaconData beaconData = beaconDataService.find(issueCreation.getBeaconId());

        Issue issue = repository.save(Issue.create(beaconData, issueCreation));
        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        BeaconIssue beaconIssue = BeaconIssue.fromIssue(issue, beacon, null);

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notifyNewBeaconIssue(issue, authorizedUser);

        return beaconIssue;
    }

    @Override
    public BeaconIssue update(long id, IssueUpdate issueUpdate) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);

        boolean issueUpdated = false;
        boolean ticketIdUpdated = false;
        Long oldTicketId = issue.getTicketId();

        if (!issue.getProblem().equals(issueUpdate.getProblem())
                || !issue.getProblemDescription().equals(issueUpdate.getProblemDescription())) {
            issueUpdated = true;

            issue.setProblem(issueUpdate.getProblem());
            issue.setProblemDescription(issueUpdate.getProblemDescription());
        }

        if ((oldTicketId == null && issueUpdate.getTicketId() != null) || !oldTicketId.equals(issueUpdate.getTicketId())) {
            ticketIdUpdated = true;

            issue.setTicketId(issueUpdate.getTicketId());
        }

        issue = repository.save(issue);

        Beacon beacon = beaconService.find(issue.getBeaconData().getId());
        IssueComment issueComment = null;

        if (issue.isResolved())
            issueComment = issueCommentService.findLastCommentByIssue(issue);

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (ticketIdUpdated)
            notifyIssueTicketIdUpdate(issue, oldTicketId, authorizedUser);

        if (issueUpdated)
            notifyBeaconIssueUpdate(issue, authorizedUser);

        return BeaconIssue.fromIssue(issue, beacon, issueComment);
    }

    @Override
    @Transactional
    public BaseMessage delete(long issueId) {
        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authorizedUser.isAdmin())
            throw new InsufficientRightsException();
        return repository.findById(issueId).map(
                issue -> {
                    issueCommentService.deleteAllByIssue(issue);
                    repository.delete(issue);

                    return new BaseMessage("Issue deleted");
                }
        ).orElseThrow(IssueNotFoundException::new);
    }

    @Override
    @Transactional
    public BeaconIssue resolve(long id, IssueSolution issueSolution) {
        throw new NotImplementedException();
    }

    @Override
    @Transactional
    public BeaconIssue updateStatus(long id, IssueStatusChange issueStatusChange) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);
        boolean notify = issue.isResolved() != issueStatusChange.isResolved();
        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (issueStatusChange.isResolved() && !issue.isResolved()) {
            issue.setResolveDate(new Date());
            issue.setResolver(authorizedUser.getUsername());
        }
        if (!issueStatusChange.isResolved() && issue.getResolveDate() != null)
            issue.setResolveDate(null);
        if (!issueStatusChange.isResolved() && issue.getResolver() != null)
            issue.setResolver(null);
        issue.setResolved(issueStatusChange.isResolved());
        issue = repository.save(issue);

        IssueComment issueComment = null;

        if (notify) {
            issueComment = issueCommentService.createStatusChangeComment(issue, issue.isResolved() ? "closed" : "reopened");
        }

        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        if (issueComment == null)
            issueComment = issueCommentService.findLastCommentByIssue(issue);

        BeaconIssue beaconIssue = BeaconIssue.fromIssue(issue, beacon, issueComment);

        if (notify) {
            notifyBeaconIssueStatusChange(issue, authorizedUser);
        }

        return beaconIssue;
    }

    @Override
    @Transactional
    public List<IssueComment> findAllComments(long issueId) {
        Issue issue = repository.findById(issueId).orElseThrow(IssueNotFoundException::new);
        return issueCommentService.findAllComments(issue);
    }

    @Override
    @Transactional
    public List<IssueComment> createComment(long issueId, IssueCommentCreation issueCommentCreation) {
        Issue issue = repository.findById(issueId).orElseThrow(IssueNotFoundException::new);

        boolean notifyClose = false;

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (issueCommentCreation.isCloseOnComment() && !issue.isResolved()) {
            issue.setResolveDate(new Date());
            issue.setResolved(true);
            issue.setResolver(authorizedUser.getUsername());
            issue = repository.save(issue);
            notifyClose = true;
        }

        IssueComment issueComment = issueCommentService.create(issue, issueCommentCreation);
        List<IssueComment> issueComments = Lists.newArrayList(issueComment);

        notifyNewBeaconIssueComment(issue, issueComment, authorizedUser);

        if (notifyClose) {
            issueComments.add(issueCommentService.createStatusChangeComment(issue, "closed"));
            notifyBeaconIssueStatusChange(issue, authorizedUser);
        }

        return issueComments;
    }

    @Override
    @Transactional
    public IssueComment updateComment(long issueId, long commentId, IssueCommentUpdate issueCommentUpdate) {
        Issue issue = repository.findById(issueId).orElseThrow(IssueNotFoundException::new);
        IssueComment issueComment = issueCommentService.update(issue, commentId, issueCommentUpdate);

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        notifyNewBeaconIssueCommentUpdate(issue, issueComment, authorizedUser, "updated");
        return issueComment;
    }

    @Override
    @Transactional
    public BaseMessage deleteComment(long issueId, long commentId) {
        Issue issue = repository.findById(issueId).orElseThrow(IssueNotFoundException::new);
        IssueComment issueComment = issueCommentService.find(commentId);
        BaseMessage baseMessage = issueCommentService.delete(issue, commentId);

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        notifyNewBeaconIssueCommentUpdate(issue, issueComment, authorizedUser, "deleted");
        return baseMessage;
    }

    @Transactional
    public List<String> findAllIssueEmails(Issue issue) {
        String reporterEmail = repository.findReporterMailAddress(issue);
        List<String> commentEmails = issueCommentService.findAllUserEmailsByIssue(issue);
        if (reporterEmail != null)
            commentEmails.add(reporterEmail);
        return commentEmails;
    }

    private void notifyNewBeaconIssue(Issue issue, User user) {
        notifyBeaconIssueMessage(issue,
                new String[]{beaconSuedtirolConfiguration.getIssueEmailTo(), user.getEmail()},
                String.format("New issue for beacon %s",
                        issue.getBeaconData().getName()),
                String.format(
                        "A new issue has been reported by <b>%s</b> for beacon <b>%s</b>:<br/><h2>%s</h2><p style=\"white-space: pre-wrap\">%s<p>",
                        issue.getReporter(),
                        issue.getBeaconData().getName(),
                        issue.getProblem(),
                        issue.getProblemDescription())
        );
    }

    private void notifyBeaconIssueUpdate(Issue issue, User user) {
        notifyBeaconIssueMessage(issue,
                new String[]{beaconSuedtirolConfiguration.getIssueEmailTo()},
                String.format("Issue update for beacon %s",
                        issue.getBeaconData().getName()),
                String.format(
                        "An issue has been updated by <b>%s</b> for beacon <b>%s</b>:<br/><h2>%s</h2><p style=\"white-space: pre-wrap\">%s<p>",
                        user.getUsername(),
                        issue.getBeaconData().getName(),
                        issue.getProblem(),
                        issue.getProblemDescription())
        );
    }

    private void notifyIssueTicketIdUpdate(Issue issue, Long oldTicketId, User user) {
        String ticketIdNotice;
        if (oldTicketId != null)
            ticketIdNotice = String.format(
                    "The Ticket-Id of an issue has been updated from #%s to #%s by <b>%s</b>",
                    oldTicketId,
                    issue.getTicketId(),
                    user.getUsername());
        else
            ticketIdNotice = String.format(
                    "The Ticket-Id of an issue has been updated to #%s by <b>%s</b>",
                    issue.getTicketId(),
                    user.getUsername());
        notifyBeaconIssueMessage(issue,
                new String[]{beaconSuedtirolConfiguration.getIssueEmailTo()},
                String.format("New Ticket-Id for issue",
                        issue.getBeaconData().getName()),
                String.format(
                        "%s for beacon <b>%s</b>:<br/><h2>%s</h2><p style=\"white-space: pre-wrap\">%s<p>",
                        ticketIdNotice,
                        issue.getBeaconData().getName(),
                        issue.getProblem(),
                        issue.getProblemDescription())
        );
    }

    private void notifyNewBeaconIssueComment(Issue issue, IssueComment issueComment, User user) {
        List<String> emails = findAllIssueEmails(issue);
        emails.add(0, beaconSuedtirolConfiguration.getIssueEmailTo());

        notifyBeaconIssueMessage(issue,
                emails.stream().distinct().filter(s -> !s.equals(user.getEmail())).toArray(String[]::new),
                issue.getProblem(),
                String.format(
                        "<b>%s</b> commented:<p style=\"white-space: pre-wrap\">%s<p>",
                        issueComment.getUserUsername(),
                        issueComment.getComment())
        );
    }

    private void notifyNewBeaconIssueCommentUpdate(Issue issue, IssueComment issueComment, User user, String updateText) {
        List<String> emails = findAllIssueEmails(issue);
        emails.add(0, beaconSuedtirolConfiguration.getIssueEmailTo());

        notifyBeaconIssueMessage(issue,
                new String[]{beaconSuedtirolConfiguration.getIssueEmailTo()},
                issue.getProblem(),
                String.format(
                        "<b>%s</b> has %s a comment:<p style=\"white-space: pre-wrap\">%s<p>",
                        user.getUsername(),
                        updateText,
                        issueComment.getComment())
        );
    }

    private void notifyBeaconIssueStatusChange(Issue issue, User user) {
        List<String> emails = findAllIssueEmails(issue);
        emails.add(0, beaconSuedtirolConfiguration.getIssueEmailTo());

        notifyBeaconIssueMessage(issue,
                emails.stream().distinct().filter(s -> !s.equals(user.getEmail())).toArray(String[]::new),
                issue.getProblem(),
                String.format(
                        "<p>Issue was %s by %s<p>",
                        issue.isResolved() ? "closed" : "reopened",
                        user.getUsername())
        );
    }

    private void notifyBeaconIssueMessage(Issue issue, String[] tos, String subject, String messageHtml) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject(String.format("[issues.opendatahub.com #%s] %s",
                    issue.getTicketId() != null ? issue.getTicketId() : "",
                    subject));
            helper.setFrom(beaconSuedtirolConfiguration.getIssueEmailFrom());
            helper.setTo(tos);
            helper.setText(String.format(
                    "%s<span>---</span><br/><a href=\"%s%s%s%s%s\"> View Issue</a>",
                    messageHtml,
                    beaconSuedtirolConfiguration.getPasswordResetURL(),
                    "/#/issues/beacon/",
                    issue.getBeaconData().getId(),
                    "/issue/",
                    issue.getId()
            ), true);
            log.info("BeaconIssueNotificationMail to: {}", tos);
            log.info("BeaconIssueNotificationMail subject: {}", helper.getMimeMessage().getSubject());
            log.info("BeaconIssueNotificationMail content {}", messageHtml);
            emailSender.send(message);
        } catch (Exception e) {
        }
    }

    private List<BeaconIssue> mapIssuesToBeaconIssues(List<Issue> issues) {
        Map<String, Beacon> beacons = beaconService.findAllWithIds(issues.stream()
                .map(issue -> issue.getBeaconData().getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Beacon::getId, Function.identity()));

        Map<Long, IssueComment> lastCommentMap = issueCommentService.findLastCommentByIssuesMap(issues.stream().filter(issue -> issue.isResolved()).collect(Collectors.toList()));

        return issues.stream().map(issue -> BeaconIssue.fromIssue(issue, beacons.get(issue.getBeaconData().getId()), lastCommentMap.get(issue.getId())))
                .collect(Collectors.toList());
    }
}
