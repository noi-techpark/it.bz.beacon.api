package it.bz.beacon.api.service.beacon;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.repository.BeaconDataRepository;
import it.bz.beacon.api.exception.db.BeaconDataNotFoundException;
import it.bz.beacon.api.model.BeaconUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BeaconDataService implements IBeaconDataService {

    @Autowired
    private BeaconDataRepository repository;

    @Override
    @Transactional
    public List<BeaconData> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public List<BeaconData> findAllById(List<String> ids) {
        return repository.findAllById(ids);
    }

    @Override
    @Transactional
    public BeaconData find(String id) throws BeaconDataNotFoundException {
        return repository.findById(id).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    @Transactional
    public BeaconData create(BeaconData beaconData) {
        return repository.save(beaconData);
    }

    @Override
    @Transactional
    public BeaconData update(String id, BeaconUpdate beaconUpdate) throws BeaconDataNotFoundException {
        return repository.findById(id).map(beaconData -> {
            beaconData.setName(beaconUpdate.getName());
            beaconData.setDescription(beaconUpdate.getDescription());
            beaconData.setLat(beaconUpdate.getLat());
            beaconData.setLng(beaconUpdate.getLng());
            beaconData.setLocationDescription(beaconUpdate.getLocationDescription());
            beaconData.setLocationType(beaconUpdate.getLocationType());

            return repository.save(beaconData);
        }).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    @Transactional
    public ResponseEntity<?> delete(String id) throws BeaconDataNotFoundException {
        return repository.findById(id).map(
                beaconData -> {
                    repository.delete(beaconData);

                    return ResponseEntity.ok().build();
                }
        ).orElseThrow(BeaconDataNotFoundException::new);
    }
}
