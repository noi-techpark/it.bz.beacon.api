package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InfoRepository extends JpaRepository<Info, String> {
    Optional<Info> findByNamespaceAndInstanceId(String namespace, String instanceId);
    Optional<Info> findByUuidAndMajorAndMinor(String uuid, int major, int minor);
}