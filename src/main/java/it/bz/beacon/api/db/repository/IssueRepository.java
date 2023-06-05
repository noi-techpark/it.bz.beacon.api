// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.IssueSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllByResolvedIsFalse();
    List<Issue> findAllByBeaconDataAndSolution(BeaconData beaconData, IssueSolution issueSolution);
    List<Issue> findAllByBeaconData(BeaconData beaconData);

    @Query(value = "SELECT u.email FROM User u WHERE u.username = :#{#issue.reporter}")
    String findReporterMailAddress(Issue issue);
}