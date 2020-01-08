package it.bz.beacon.api.service.beacon;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.exception.db.BeaconDataNotFoundException;
import it.bz.beacon.api.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.api.model.BeaconUpdate;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBeaconDataService {
    List<BeaconData> findAll();

    List<BeaconData> findAllByGroupId(Long groupId);
    List<BeaconData> findAllById(List<String> ids);
    BeaconData find(String id) throws BeaconDataNotFoundException;
    BeaconData create(BeaconData beaconData);
    BeaconData update(String id, BeaconUpdate beaconUpdate) throws BeaconDataNotFoundException;
    ResponseEntity<?> delete(String id) throws BeaconDataNotFoundException;
    BeaconData updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate) throws BeaconDataNotFoundException;
}
