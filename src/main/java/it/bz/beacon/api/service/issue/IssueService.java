package it.bz.beacon.api.service.issue;

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
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
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

    @Override
    public List<BeaconIssue> findAll(boolean onlyUnresolved) {
        List<Issue> issues = onlyUnresolved ? repository.findAllBySolution(null) : repository.findAll();

        Map<Long, Beacon> beacons = beaconService.findAllWithIds(issues.stream()
                .map(issue -> issue.getBeaconData().getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Beacon::getId, Function.identity()));

        return issues.stream().map(issue -> BeaconIssue.fromIssue(issue, beacons.get(issue.getBeaconData().getId())))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public List<BeaconIssue> findAllByBeacon(BeaconData beaconData, boolean onlyUnresolved) {
        List<Issue> issues = onlyUnresolved ? repository.findAllByBeaconDataAndSolution(beaconData, null) : repository.findAllByBeaconData(beaconData);

        Map<Long, Beacon> beacons = beaconService.findAllWithIds(issues.stream()
                .map(issue -> issue.getBeaconData().getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Beacon::getId, Function.identity()));

        return issues.stream().map(issue -> BeaconIssue.fromIssue(issue, beacons.get(issue.getBeaconData().getId())))
                .collect(Collectors.toList());

    }

    @Override
    public BeaconIssue find(long id) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);
        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        return BeaconIssue.fromIssue(issue, beacon);
    }

    @Override
    public BeaconIssue create(IssueCreation issueCreation) {
        BeaconData beaconData = beaconDataService.find(issueCreation.getBeaconId());

        Issue issue = repository.save(Issue.create(beaconData, issueCreation));
        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        return BeaconIssue.fromIssue(issue, beacon);
    }

    @Override
    public BeaconIssue resolve(long id, IssueSolution issueSolution) {
        Issue issue = repository.findById(id).orElseThrow(IssueNotFoundException::new);
        issue.setSolution(issueSolution);
        issue = repository.save(issue);

        Beacon beacon = beaconService.find(issue.getBeaconData().getId());

        return BeaconIssue.fromIssue(issue, beacon);
    }
}
