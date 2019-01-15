package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.NetworkProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NetworkProblemRepository extends JpaRepository<NetworkProblem, Long> {
    List<NetworkProblem> findByBeaconId(long beaconId);
}
