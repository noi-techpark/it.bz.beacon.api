package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.BeaconData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeaconDataRepository extends JpaRepository<BeaconData, String> {
    List<BeaconData> findAllByGroupId(Long groupId);

}
