package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.BeaconData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeaconDataRepository extends JpaRepository<BeaconData, Long> {
}
