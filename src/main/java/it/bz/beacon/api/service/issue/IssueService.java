package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueSolution;
import it.bz.beacon.api.db.repository.IssueRepository;
import it.bz.beacon.api.exception.db.IssueNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.model.BeaconIssue;
import it.bz.beacon.api.model.IssueCreation;
import it.bz.beacon.api.service.beacon.IBeaconDataService;
import it.bz.beacon.api.service.beacon.IBeaconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
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

    @Override
    @Transactional
    public List<BeaconIssue> findAll(boolean onlyUnresolved) {
        List<Issue> issues = onlyUnresolved ? repository.findAllBySolution(null) : repository.findAll();

        Map<String, Beacon> beacons = beaconService.findAllWithIds(issues.stream()
                .map(issue -> issue.getBeaconData().getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Beacon::getId, Function.identity()));

        return issues.stream().map(issue -> BeaconIssue.fromIssue(issue, beacons.get(issue.getBeaconData().getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BeaconIssue> findAllByBeacon(BeaconData beaconData, boolean onlyUnresolved) {
        List<Issue> issues = onlyUnresolved ? repository.findAllByBeaconDataAndSolution(beaconData, null) : repository.findAllByBeaconData(beaconData);

        Map<String, Beacon> beacons = beaconService.findAllWithIds(issues.stream()
                .map(issue -> issue.getBeaconData().getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Beacon::getId, Function.identity()));

        return issues.stream().map(issue -> BeaconIssue.fromIssue(issue, beacons.get(issue.getBeaconData().getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BeaconIssue find(long id) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);
        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        return BeaconIssue.fromIssue(issue, beacon);
    }

    @Override
    @Transactional
    public BeaconIssue create(IssueCreation issueCreation) {
        BeaconData beaconData = beaconDataService.find(issueCreation.getBeaconId());

        Issue issue = repository.save(Issue.create(beaconData, issueCreation));
        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        BeaconIssue beaconIssue = BeaconIssue.fromIssue(issue, beacon);
        notifyNewBeaconIssue(beaconIssue);

        return beaconIssue;
    }

    @Override
    @Transactional
    public BeaconIssue resolve(long id, IssueSolution issueSolution) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);
        issue.setSolution(issueSolution);
        issue = repository.save(issue);

        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        return BeaconIssue.fromIssue(issue, beacon);
    }

    private void notifyNewBeaconIssue(BeaconIssue beaconIssue) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject("New issue for beacon " + beaconIssue.getBeacon().getName());
            helper.setFrom(beaconSuedtirolConfiguration.getIssueEmailFrom());
            helper.setTo(beaconSuedtirolConfiguration.getIssueEmailTo());
            helper.setText(String.format(
                    "A new issue has been reported by '%s' for beacon '%s':<br/><br/>%s<br/><br/>%s",
                    beaconIssue.getReporter(),
                    beaconIssue.getBeacon().getName(),
                    beaconIssue.getProblem(),
                    beaconIssue.getProblemDescription()
            ), true);
            emailSender.send(message);
        } catch (Exception e) { }
    }
}
