package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {
    IssueComment findFirstByIssueOrderByCreatedAtDesc(Issue issue);
}