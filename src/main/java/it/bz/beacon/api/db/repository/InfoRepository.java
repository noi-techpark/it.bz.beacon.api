package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InfoRepository extends JpaRepository<Info, Long> {
    Optional<Info> findByBeaconId(String beaconId);
    Optional<Info> findByEddystone(String namespace, String instanceId);
    Optional<Info> findByIBeacon(String uuid, int major, int minor);
}