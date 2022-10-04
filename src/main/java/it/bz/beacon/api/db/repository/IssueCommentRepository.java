package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {

    IssueComment findFirstByIssueOrderByCreatedAtDesc(Issue issue);

    List<IssueComment> findAllByIssueOrderByCreatedAt(Issue issue);

    Optional<IssueComment> findByIdAndIssue(long commentId, Issue issue);

    @Query(value = "SELECT distinct u.email FROM IssueComment ic join User u on ic.user = u WHERE ic.issue = :issue")
    List<String> findAllUserEmailsByIssue(Issue issue);

    void deleteAllByIssue(Issue issue);
}