package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeaconRepository extends JpaRepository<Beacon, String> {
    List<Beacon> findAllByGroupId(Long groupId);
}
