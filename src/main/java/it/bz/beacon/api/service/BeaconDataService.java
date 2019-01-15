package it.bz.beacon.api.service;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.repository.BeaconDataRepository;
import it.bz.beacon.api.exception.BeaconDataNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.model.Manufacturer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeaconDataService implements IBeaconDataService {

    @Autowired
    private BeaconDataRepository repository;

    @Override
    public List<BeaconData> findAll() {
        return repository.findAll();
    }

    @Override
    public List<BeaconData> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public BeaconData find(long id) throws BeaconDataNotFoundException {
        return repository.findById(id).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public BeaconData create(Beacon beacon, Manufacturer manufacturer, String manufacturerId) {
        return repository.save(BeaconData.fromBeacon(beacon, manufacturer, manufacturerId));
    }

    @Override
    public BeaconData update(long id, Beacon beaconDataRequest) throws BeaconDataNotFoundException {
        return repository.findById(id).map(beaconData -> {
            beaconData.setName(beaconDataRequest.getName());
            beaconData.setDescription(beaconDataRequest.getDescription());
            beaconData.setLat(beaconDataRequest.getLat());
            beaconData.setLng(beaconDataRequest.getLng());

            return repository.save(beaconData);
        }).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public ResponseEntity<?> delete(long id) throws BeaconDataNotFoundException {
        return repository.findById(id).map(
                beaconData -> {
                    repository.delete(beaconData);

                    return ResponseEntity.ok().build();
                }
        ).orElseThrow(BeaconDataNotFoundException::new);
    }
}
