package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.IssueSolution;
import it.bz.beacon.api.model.BeaconIssue;
import it.bz.beacon.api.model.IssueCreation;
import it.bz.beacon.api.model.IssueStatusChange;

import java.util.List;

public interface IIssueService {
    List<BeaconIssue> findAll(boolean onlyUnresolved);
    List<BeaconIssue> findAllByBeacon(BeaconData beaconData, boolean onlyUnresolved);
    BeaconIssue find(long id);
    BeaconIssue create(IssueCreation issueCreation);
    BeaconIssue resolve(long id, IssueSolution issueSolution);

    BeaconIssue updateStatus(long id, IssueStatusChange issueStatusChange);
}
