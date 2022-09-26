package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {

    IssueComment findFirstByIssueOrderByCreatedAtDesc(Issue issue);

    List<IssueComment> findAllByIssue(Issue issue);

    Optional<IssueComment> findByIdAndIssue(long commentId, Issue issue);
}