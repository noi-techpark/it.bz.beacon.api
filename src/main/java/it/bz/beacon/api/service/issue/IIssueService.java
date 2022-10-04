package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.IssueComment;
import it.bz.beacon.api.db.model.IssueSolution;
import it.bz.beacon.api.model.*;

import java.util.List;

public interface IIssueService {
    List<BeaconIssue> findAll(boolean onlyUnresolved);
    List<BeaconIssue> findAllByBeacon(BeaconData beaconData, boolean onlyUnresolved);
    BeaconIssue find(long id);
    BeaconIssue create(IssueCreation issueCreation);
    BeaconIssue resolve(long id, IssueSolution issueSolution);
    BeaconIssue updateStatus(long id, IssueStatusChange issueStatusChange);
    List<IssueComment> findAllComments(long issueId);

    List<IssueComment> createComment(long issueId, IssueCommentCreation issueCommentCreation);
    IssueComment updateComment(long issueId, long commentId, IssueCommentUpdate issueCommentUpdate);
    BaseMessage deleteComment(long issueId, long commentId);
    BeaconIssue update(long id, IssueUpdate issueUpdate);
    BaseMessage delete(long id);
}
