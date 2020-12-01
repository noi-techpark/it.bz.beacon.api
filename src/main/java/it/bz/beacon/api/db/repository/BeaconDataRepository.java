package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.db.model.BeaconData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BeaconDataRepository extends JpaRepository<BeaconData, String> {
    List<BeaconData> findAllByGroupId(Long groupId);


    @Query("select b from Beacon b where id = :beaconId")
    Optional<Beacon> findBeaconById(@Param("beaconId") String beaconId);

    @Query("select b from Beacon b")
    List<Beacon> findAllBeacon();

    @Query("select b from Beacon b where group_id = :groupId")
    List<Beacon> findAllBeaconByGroupId(@Param("groupId") Long groupId);

    @Query("select b from Beacon b where id in :ids")
    List<Beacon> findAllBeaconById(@Param("ids") List<String> ids);
}
