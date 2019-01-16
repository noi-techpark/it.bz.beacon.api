package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
//    List<Issue> findByUsername(String username);
}