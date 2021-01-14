package it.bz.beacon.api.service.beacon;

import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.Group;
import it.bz.beacon.api.db.repository.BeaconDataRepository;
import it.bz.beacon.api.db.repository.GroupRepository;
import it.bz.beacon.api.exception.db.BeaconDataNotFoundException;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.api.model.BeaconUpdate;
import it.bz.beacon.api.model.RemoteBeacon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class BeaconDataService implements IBeaconDataService {

    @Autowired
    private BeaconDataRepository repository;

    @Autowired
    private GroupRepository groupRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    public List<BeaconData> findAll() {
        return repository.findAll();
    }

    @Override
    public BeaconData find(String id) throws BeaconDataNotFoundException {
        return repository.findById(id).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public Beacon create(BeaconData beaconData) {
        beaconData.setUserUpdatedAt(new Date());
        String id = repository.saveAndFlush(beaconData).getId();

        em.clear();

        return repository.findBeaconById(id).orElseThrow(BeaconNotFoundException::new);
    }

    @Override
    public Beacon update(BeaconData beaconData) throws BeaconNotFoundException {
        String id = repository.saveAndFlush(beaconData).getId();
        em.clear();
        return repository.findBeaconById(id).orElseThrow(BeaconNotFoundException::new);
    }

    @Override
    public Beacon update(String id, BeaconUpdate beaconUpdate, RemoteBeacon remoteBeacon) throws BeaconDataNotFoundException {
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

            if (remoteBeacon != null) {
                beaconData.setRemoteBeaconUpdatedAt(new Date());
                beaconData.setRemoteBeacon(remoteBeacon);
            }

            repository.saveAndFlush(beaconData).getId();

            em.clear();

            return repository.findBeaconById(id).orElseThrow(BeaconNotFoundException::new);
        }).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public Beacon updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate)
            throws BeaconDataNotFoundException {
        return repository.findBeaconByIdOrManufacturerId(id).map(beaconData -> {
            beaconData.setBatteryLevel(batteryLevelUpdate.getBatteryLevel());
            beaconData.setTrustedUpdatedAt(new Date());

            repository.saveAndFlush(beaconData);

            em.clear();

            return repository.findBeaconById(id).orElseThrow(BeaconNotFoundException::new);
        }).orElseThrow(BeaconDataNotFoundException::new);
    }

    @Override
    public List<Beacon> findAllBeacon() {
        return repository.findAllBeacon();
    }

    @Override
    public List<Beacon> findAllBeaconByGroupId(Long groupId) {
        return repository.findAllBeaconByGroupId(groupId);
    }

    @Override
    public List<Beacon> findAllBeacon(List<String> ids) {
        return repository.findAllBeaconById(ids);
    }

    @Override
    public Optional<Beacon> findBeacon(String id) {
        return repository.findBeaconById(id);
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
