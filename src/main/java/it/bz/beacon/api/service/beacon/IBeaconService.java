package it.bz.beacon.api.service.beacon;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.model.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBeaconService {
    List<Beacon> findAll();
    List<Beacon> findAllWithRemoteCache();
    List<Beacon> findAllWithIds(List<String> ids);
    Beacon find(String id) throws BeaconNotFoundException;
    List<Beacon> createByOrder(ManufacturerOrder order);
    Beacon update(String id, BeaconUpdate beaconUpdate) throws BeaconNotFoundException;
    ResponseEntity<?> delete(String id) throws BeaconNotFoundException;
    Beacon updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate) throws BeaconNotFoundException;
}
