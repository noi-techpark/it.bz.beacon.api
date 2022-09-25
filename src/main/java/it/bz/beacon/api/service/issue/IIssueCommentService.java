package it.bz.beacon.api.service.issue;

import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueComment;
import it.bz.beacon.api.db.model.IssueSolution;

import java.util.List;
import java.util.Map;

public interface IIssueCommentService {
    List<IssueComment> findAll();

    Map<Long, IssueComment> findLastCommentByIssuesMap(List<Issue> issues);

    IssueComment findLastCommentByIssue(Issue issue);

    IssueComment find(long id);

    IssueComment create(Issue issue, IssueSolution issueSolution);
}
