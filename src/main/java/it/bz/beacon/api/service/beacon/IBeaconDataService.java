package it.bz.beacon.api.service.beacon;

import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.exception.db.BeaconDataNotFoundException;
import it.bz.beacon.api.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.api.model.BeaconUpdate;
import it.bz.beacon.api.model.RemoteBeacon;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IBeaconDataService {
    List<BeaconData> findAll();

    BeaconData find(String id) throws BeaconDataNotFoundException;

    Beacon create(BeaconData beaconData);

    Beacon update(BeaconData beaconData);

    Beacon update(String id, BeaconUpdate beaconUpdate, RemoteBeacon remoteBeacon) throws BeaconDataNotFoundException;
    ResponseEntity<?> delete(String id) throws BeaconDataNotFoundException;

    Beacon updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate) throws BeaconDataNotFoundException;

    List<Beacon> findAllBeacon();

    List<Beacon> findAllBeaconByGroupId(Long groupId);

    List<Beacon> findAllBeacon(List<String> ids);

    Optional<Beacon> findBeacon(String id);
}
