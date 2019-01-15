package it.bz.beacon.api.service;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.exception.BeaconDataNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.model.Manufacturer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBeaconDataService {
    List<BeaconData> findAll();
    List<BeaconData> findAllById(List<Long> ids);
    BeaconData find(long id) throws BeaconDataNotFoundException;
    BeaconData create(Beacon beacon, Manufacturer manufacturer, String manufacturerId);
    BeaconData update(long id, Beacon beacon) throws BeaconDataNotFoundException;
    ResponseEntity<?> delete(long id) throws BeaconDataNotFoundException;
}
