package it.bz.beacon.api.service.beacon;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.Group;
import it.bz.beacon.api.db.repository.BeaconDataRepository;
import it.bz.beacon.api.db.repository.GroupRepository;
import it.bz.beacon.api.exception.db.BeaconDataNotFoundException;
import it.bz.beacon.api.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.api.model.BeaconUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class BeaconDataService implements IBeaconDataService {

    @Autowired
    private BeaconDataRepository repository;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public List<BeaconData> findAllByGroupId(Long groupId) {
        return repository.findAllByGroupId(groupId);
    }

    @Override
    public List<BeaconData> findAll() {
        return repository.findAll();
    }

    @Override
    public List<BeaconData> findAllById(List<String> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public BeaconData find(String id) throws BeaconDataNotFoundException {
        return repository.findById(id).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public BeaconData create(BeaconData beaconData) {
        beaconData.setUserUpdatedAt(new Date());
        return repository.save(beaconData);
    }

    @Override
    public BeaconData update(String id, BeaconUpdate beaconUpdate) throws BeaconDataNotFoundException {
        return repository.findById(id).map(beaconData -> {
            beaconData.setName(beaconUpdate.getName());
            beaconData.setDescription(beaconUpdate.getDescription());
            beaconData.setLatBeacon(beaconUpdate.getLat());
            beaconData.setLngBeacon(beaconUpdate.getLng());
            beaconData.setLocationDescription(beaconUpdate.getLocationDescription());
            beaconData.setLocationType(beaconUpdate.getLocationType());

            beaconData.setGroup(null);
            if (beaconUpdate.getGroup() != null)
            {
                long groupId = beaconUpdate.getGroup();
                Group group = groupRepository.findById(groupId).get();
                beaconData.setGroup(group);
            }

            if (beaconUpdate.getInfo() != null) {
                beaconData.setNamePoi(beaconUpdate.getInfo().getName());
                beaconData.setAddress(beaconUpdate.getInfo().getAddress());
                beaconData.setCap(beaconUpdate.getInfo().getCap());
                beaconData.setLocation(beaconUpdate.getInfo().getLocation());
                beaconData.setLatPoi(beaconUpdate.getInfo().getLatitude());
                beaconData.setLngPoi(beaconUpdate.getInfo().getLongitude());
                beaconData.setFloor(beaconUpdate.getInfo().getFloor());
            }

            beaconData.setUserUpdatedAt(new Date());

            return repository.save(beaconData);
        }).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public BeaconData updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate)
            throws BeaconDataNotFoundException {
        return repository.findById(id).map(beaconData -> {
            beaconData.setBatteryLevel(batteryLevelUpdate.getBatteryLevel());
            beaconData.setTrustedUpdatedAt(new Date());

            return repository.save(beaconData);
        }).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public ResponseEntity<?> delete(String id) throws BeaconDataNotFoundException {
        return repository.findById(id).map(
                beaconData -> {
                    repository.delete(beaconData);

                    return ResponseEntity.ok().build();
                }
        ).orElseThrow(BeaconDataNotFoundException::new);
    }
}
