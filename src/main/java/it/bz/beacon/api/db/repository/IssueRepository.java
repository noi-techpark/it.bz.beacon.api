package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueSolution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllBySolution(IssueSolution issueSolution);
    List<Issue> findAllByBeaconDataAndSolution(BeaconData beaconData, IssueSolution issueSolution);
    List<Issue> findAllByBeaconData(BeaconData beaconData);
}