package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface InfoRepository extends JpaRepository<Info, String> {
    Optional<Info> findByNamespaceAndInstanceId(String namespace, String instanceId);
    Optional<Info> findByUuidAndMajorAndMinor(UUID uuid, int major, int minor);

    @Query("SELECT coalesce(max(i.major), 0) FROM Info i")
    Integer getMaxMajor();

    @Query("SELECT coalesce(max(i.minor), 0) FROM Info i where i.major = ?1")
    Integer getMaxMinor(int major);
}