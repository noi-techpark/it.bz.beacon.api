package it.bz.beacon.api.service.beacon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.exception.db.BeaconConfigurationNotCreatedException;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.kontakt.io.ApiService;
import it.bz.beacon.api.kontakt.io.model.TagBeaconConfig;
import it.bz.beacon.api.kontakt.io.response.BeaconListResponse;
import it.bz.beacon.api.kontakt.io.response.DefaultResponse;
import it.bz.beacon.api.kontakt.io.response.DeviceStatusListResponse;
import it.bz.beacon.api.kontakt.io.response.ConfigurationListResponse;
import it.bz.beacon.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BeaconService implements IBeaconService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private IBeaconDataService beaconDataService;

    @Override
    public List<Beacon> findAll() {
        List<BeaconData> beaconDatas = beaconDataService.findAll();
        Map<String, RemoteBeacon> remoteBeacons = getRemoteBeacons(beaconDatas);

        return beaconDatas.stream()
                .map(beaconData -> Beacon.fromRemoteBeacon(beaconData, remoteBeacons.get(beaconData.getManufacturerId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Beacon> findAllWithIds(List<Long> ids) {
        List<BeaconData> beaconDatas = beaconDataService.findAllById(ids);
        Map<String, RemoteBeacon> remoteBeacons = getRemoteBeacons(beaconDatas);

        return beaconDatas.stream()
                .map(beaconData -> Beacon.fromRemoteBeacon(beaconData, remoteBeacons.get(beaconData.getManufacturerId())))
                .collect(Collectors.toList());
    }

    @Override
    public Beacon find(long id) throws BeaconNotFoundException {
        BeaconData beaconData = beaconDataService.find(id);
        Map<String, RemoteBeacon> remoteBeacons = getRemoteBeacons(Lists.newArrayList(beaconData));

        return Beacon.fromRemoteBeacon(beaconData, remoteBeacons.get(beaconData.getManufacturerId()));
    }

    @Override
    public Beacon create(Beacon beacon) {
        return null;
    }

    @Override
    public List<Beacon> createByOrder(String orderId) {
        List<String> uniqueIds = apiService.checkOrder(orderId).getUniqueIds();
        if (uniqueIds.size() <= 0) {
            //TODO handle used order Id
        }

        long addedDevices = apiService.assignOrder(orderId).getAddedDevices();

        if (uniqueIds.size() != addedDevices) {
            //TODO handle ambiguous amount of devices added
        }

        List<Beacon> beacons = Lists.newArrayList();

        for(String uniqueId : uniqueIds) {
            Beacon beacon = new Beacon();
            beacon.setName(uniqueId);
            beacon.setManufacturer(Manufacturer.KONTAKT_IO);
            beacon.setManufacturerId(uniqueId);
            beaconDataService.create(beacon);
            beacons.add(beacon);
        }

        return beacons;
    }

    @Override
    public Beacon update(long id, BeaconUpdate beaconUpdate) throws BeaconNotFoundException {
        BeaconData beaconData = beaconDataService.find(id);

        CompletableFuture<ResponseEntity<DefaultResponse>> configResponse = createConfig(beaconData.getManufacturerId(), beaconUpdate);

        CompletableFuture.allOf(configResponse).join();

        try {
            if (configResponse.get().getStatusCode() != HttpStatus.OK) {
                throw new BeaconConfigurationNotCreatedException();
            }

            Beacon beacon = find(id);
            beacon.applyBeaconData(beaconData);

            return beacon;
        } catch (InterruptedException | ExecutionException e) {
            throw new BeaconConfigurationNotCreatedException();
        }
    }

    @Async
    private CompletableFuture<ResponseEntity<DefaultResponse>> createConfig(String uniqueId, BeaconUpdate beaconUpdate) {
        TagBeaconConfig config = new TagBeaconConfig();
        config.setUniqueId(uniqueId);
        config.setProximity(beaconUpdate.getUuid());
        config.setMajor(beaconUpdate.getMajor());
        config.setMinor(beaconUpdate.getMinor());

        //TODO set other values for config

        return CompletableFuture.completedFuture(apiService.createConfig(Lists.newArrayList(config)));
    }

    @Override
    public ResponseEntity<?> delete(long id) throws BeaconNotFoundException {
        return null;
    }

    private Map<String, RemoteBeacon> getRemoteBeacons(List<BeaconData> beaconDatas) {
        BeaconListResponse response = apiService.getBeacons(beaconDatas.stream()
                .map(BeaconData::getManufacturerId)
                .collect(Collectors.toList()));

        return getBeaconsWithStatuses(response);
    }

    private Map<String, RemoteBeacon> getBeaconsWithStatuses(BeaconListResponse response) {
        if (response == null || response.getDevices() == null) {
            return Maps.newHashMap();
        }

        Map<String, RemoteBeacon> remoteBeacons = response.getDevices().stream()
                .map(RemoteBeacon::fromTagBeaconDevice)
                .collect(Collectors.toMap(RemoteBeacon::getManufacturerId, Function.identity()));

        DeviceStatusListResponse statusListResponse = apiService.getDeviceStatuses(new ArrayList<>(remoteBeacons.keySet()));
        if (statusListResponse != null && statusListResponse.getStatuses() != null) {
            statusListResponse.getStatuses().forEach(status -> {
                RemoteBeacon remoteBeacon = remoteBeacons.get(status.getUniqueId());
                if (remoteBeacon != null) {
                    remoteBeacon.setBatteryLevel(status.getBatteryLevel());
                }
            });
        }

        ConfigurationListResponse configListResponse = apiService.getConfigurations(new ArrayList<>(remoteBeacons.keySet()));
        if (configListResponse != null && configListResponse.getConfigs() != null) {
            configListResponse.getConfigs().forEach(configuration -> {
                RemoteBeacon remoteBeacon = remoteBeacons.get(configuration.getUniqueId());
                if (remoteBeacon != null) {
                    remoteBeacon.setPendingConfiguration(PendingConfiguration.fromBeaconConfiguration(configuration));
                }
            });
        }
        return remoteBeacons;
    }
}
