package it.bz.beacon.api.service;

import it.bz.beacon.api.exception.BeaconNotFoundException;
import it.bz.beacon.api.model.Beacon;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBeaconService {
    List<Beacon> findAll();
    List<Beacon> findAllWithIds(List<Long> ids);
    Beacon find(long id) throws BeaconNotFoundException;
    Beacon create(Beacon beacon);
    List<Beacon> createByOrder(String orderId);
    Beacon update(long id, Beacon beacon) throws BeaconNotFoundException;
    ResponseEntity<?> delete(long id) throws BeaconNotFoundException;
}
