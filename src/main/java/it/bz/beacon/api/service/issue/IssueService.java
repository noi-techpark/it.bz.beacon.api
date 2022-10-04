package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.model.*;
import it.bz.beacon.api.db.repository.IssueRepository;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.db.IssueNotFoundException;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.service.beacon.IBeaconDataService;
import it.bz.beacon.api.service.beacon.IBeaconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
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
        notifyNewBeaconIssue(issue);

        return beaconIssue;
    }

    @Override
    public BeaconIssue update(long id, IssueUpdate issueUpdate) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);

        issue.setProblem(issueUpdate.getProblem());
        issue.setProblemDescription(issueUpdate.getProblemDescription());
        issue.setTicketId(issueUpdate.getTicketId());

        issue = repository.save(issue);

        Beacon beacon = beaconService.find(issue.getBeaconData().getId());
        IssueComment issueComment = null;

        if (issue.isResolved())
            issueComment = issueCommentService.findLastCommentByIssue(issue);

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
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);
        issue.setSolution(issueSolution);
        issue.setResolved(true);
        issue.setResolveDate(new Date());
        issue = repository.save(issue);

        IssueComment issueComment = issueCommentService.create(issue, issueSolution);

        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        return BeaconIssue.fromIssue(issue, beacon, issueComment);
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

        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        IssueComment issueComment = issueCommentService.findLastCommentByIssue(issue);

        BeaconIssue beaconIssue = BeaconIssue.fromIssue(issue, beacon, issueComment);

        if (notify) {
            notifyBeaconIssueStatusChange(issue, authorizedUser.getUsername());
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
    public IssueComment createComment(long issueId, IssueCommentCreation issueCommentCreation) {
        Issue issue = repository.findById(issueId).orElseThrow(IssueNotFoundException::new);

        boolean notifyClose = false;

        if (issueCommentCreation.isCloseOnComment() && !issue.isResolved()) {
            User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            issue.setResolveDate(new Date());
            issue.setResolved(true);
            issue.setResolver(authorizedUser.getUsername());
            issue = repository.save(issue);
            notifyClose = true;
        }

        IssueComment issueComment = issueCommentService.create(issue, issueCommentCreation);

        notifyNewBeaconIssueComment(issue, issueComment);

        if (notifyClose)
            notifyBeaconIssueStatusChange(issue, issueComment.getUserUsername());

        return issueComment;
    }

    @Override
    @Transactional
    public IssueComment updateComment(long issueId, long commentId, IssueCommentUpdate issueCommentUpdate) {
        Issue issue = repository.findById(issueId).orElseThrow(IssueNotFoundException::new);
        return issueCommentService.update(issue, commentId, issueCommentUpdate);
    }

    @Override
    @Transactional
    public BaseMessage deleteComment(long issueId, long commentId) {
        Issue issue = repository.findById(issueId).orElseThrow(IssueNotFoundException::new);
        return issueCommentService.delete(issue, commentId);
    }

    @Transactional
    public String[] findAllIssueEmails(Issue issue) {
        String reporterEmail = repository.findReporterMailAddress(issue);
        List<String> commentEmails = issueCommentService.findAllUserEmailsByIssue(issue);
        if (reporterEmail != null)
            commentEmails.add(reporterEmail);
        return commentEmails.stream().distinct().toArray(String[]::new);
    }

    private void notifyNewBeaconIssue(Issue issue) {
        notifyBeaconIssueMessage(issue,
                new String[]{beaconSuedtirolConfiguration.getIssueEmailFrom()},
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

    private void notifyNewBeaconIssueComment(Issue issue, IssueComment issueComment) {
        notifyBeaconIssueMessage(issue,
                findAllIssueEmails(issue),
                issue.getProblem(),
                String.format(
                        "<b>%s</b> commented:<p style=\"white-space: pre-wrap\">%s<p>",
                        issueComment.getUserUsername(),
                        issueComment.getComment())
        );
    }

    private void notifyBeaconIssueStatusChange(Issue issue, String reported) {
        notifyBeaconIssueMessage(issue,
                findAllIssueEmails(issue),
                issue.getProblem(),
                String.format(
                        "<p>Issue was %s by %s<p>",
                        issue.isResolved() ? "closed" : "reopened",
                        reported)
        );
    }

    private void notifyBeaconIssueMessage(Issue issue, String[] tos, String subject, String messageHtml) {
        try {
            System.out.println(Arrays.toString(tos));
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject(String.format("[issues.opendatahub.bz.it #%s] %s",
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
