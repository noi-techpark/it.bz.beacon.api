package it.bz.beacon.api.service.beacon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.exception.db.BeaconConfigurationNotCreatedException;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.exception.kontakt.io.InvalidOrderIdException;
import it.bz.beacon.api.exception.kontakt.io.NoDeviceAddedException;
import it.bz.beacon.api.kontakt.io.ApiService;
import it.bz.beacon.api.kontakt.io.model.BeaconConfigResponse;
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
    public List<Beacon> createByOrder(Order order) {
        List<String> uniqueIds = apiService.checkOrder(order.getId());
        if (uniqueIds.size() <= 0) {
            throw new InvalidOrderIdException();
        }

        long addedDevices = apiService.assignOrder(order.getId()).getAddedDevices();

        if (addedDevices <= 0) {
            throw new NoDeviceAddedException();
        }

        return apiService.getBeacons(uniqueIds).getDevices().stream().map(tagBeaconDevice -> {

            RemoteBeacon remoteBeacon = RemoteBeacon.fromTagBeaconDevice(tagBeaconDevice);
            BeaconData beaconData = beaconDataService.create(BeaconData.fromRemoteBeacon(remoteBeacon));

            return Beacon.fromRemoteBeacon(beaconData, remoteBeacon);
        }).collect(Collectors.toList());
    }

    @Override
    public Beacon update(long id, BeaconUpdate beaconUpdate) throws BeaconNotFoundException {
        BeaconData beaconData = beaconDataService.find(id);

        CompletableFuture<ResponseEntity<List<BeaconConfigResponse>>> configResponse = createConfig(beaconData, beaconUpdate);

        CompletableFuture.allOf(configResponse).join();

        try {
            if (configResponse.get().getStatusCode() != HttpStatus.CREATED) {
                throw new BeaconConfigurationNotCreatedException();
            }

            Beacon beacon = find(id);

            beacon.applyBeaconData(beaconDataService.update(id, beaconUpdate));

            return beacon;
        } catch (InterruptedException | ExecutionException e) {
            throw new BeaconConfigurationNotCreatedException();
        }
    }

    @Async
    private CompletableFuture<ResponseEntity<List<BeaconConfigResponse>>> createConfig(BeaconData beaconData, BeaconUpdate beaconUpdate) {
        return CompletableFuture.completedFuture(apiService.createConfig(TagBeaconConfig.fromBeaconUpdate(beaconUpdate, beaconData)));
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
                    remoteBeacon.setLastSeen(status.getLastEventTimestamp());
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
