package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.db.model.IssueSolution;
import it.bz.beacon.api.model.BaseMessage;
import it.bz.beacon.api.model.BeaconIssue;
import it.bz.beacon.api.model.IssueCreation;

import java.util.List;

public interface IIssueService {
    List<BeaconIssue> findAll(boolean onlyResolved);
    BeaconIssue find(long id);
    BeaconIssue create(IssueCreation issueCreation);
    BeaconIssue resolve(long id, IssueSolution issueSolution);
    BaseMessage delete(long id);
}
