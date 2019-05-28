package it.bz.beacon.api.service.beacon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.exception.db.*;
import it.bz.beacon.api.kontakt.io.ApiService;
import it.bz.beacon.api.kontakt.io.model.BeaconConfigDeletionResponse;
import it.bz.beacon.api.kontakt.io.model.BeaconConfigResponse;
import it.bz.beacon.api.kontakt.io.model.TagBeaconConfig;
import it.bz.beacon.api.kontakt.io.model.TagBeaconDevice;
import it.bz.beacon.api.kontakt.io.model.enumeration.Packet;
import it.bz.beacon.api.kontakt.io.model.enumeration.Profile;
import it.bz.beacon.api.kontakt.io.response.BeaconListResponse;
import it.bz.beacon.api.kontakt.io.response.ConfigurationListResponse;
import it.bz.beacon.api.kontakt.io.response.DeviceStatusListResponse;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.util.ManufacturerNameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
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
    public List<Beacon> findAllWithIds(List<String> ids) {
        if (ids.size() <= 0) {
            return Lists.newArrayList();
        }

        List<BeaconData> beaconDatas = beaconDataService.findAllById(ids);
        Map<String, RemoteBeacon> remoteBeacons = getRemoteBeacons(beaconDatas);

        return beaconDatas.stream()
                .map(beaconData -> Beacon.fromRemoteBeacon(beaconData, remoteBeacons.get(beaconData.getManufacturerId())))
                .collect(Collectors.toList());
    }

    @Override
    public Beacon find(String id) throws BeaconNotFoundException {
        BeaconData beaconData = beaconDataService.find(id);
        Map<String, RemoteBeacon> remoteBeacons = getRemoteBeacons(Lists.newArrayList(beaconData));

        return Beacon.fromRemoteBeacon(beaconData, remoteBeacons.get(beaconData.getManufacturerId()));
    }

    @Override
    public List<Beacon> createByOrder(ManufacturerOrder order) {
        try {
            apiService.assignOrder(order.getId());
        } catch (HttpClientErrorException e) {

        }

        return fetchBeaconsFromManufacturer(order.getId()).stream().map(tagBeaconDevice -> {
                try {
                    RemoteBeacon remoteBeacon = RemoteBeacon.fromTagBeaconDevice(tagBeaconDevice);
                    BeaconData beaconData;
                    try {
                        beaconData = beaconDataService.find(remoteBeacon.getId());
                    } catch (BeaconDataNotFoundException e) {
                        beaconData = null;
                    }
                    if (beaconData != null) {
                        return null;
                    }

                    beaconData = beaconDataService.create(BeaconData.fromRemoteBeacon(remoteBeacon));

                    return Beacon.fromRemoteBeacon(beaconData, remoteBeacon);
                } catch (InvalidBeaconIdentifierException e) {
                    return null;
                }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<TagBeaconDevice> fetchBeaconsFromManufacturer(String orderId) {
        return fetchBeaconsFromManufacturer(orderId, 0);
    }

    private List<TagBeaconDevice> fetchBeaconsFromManufacturer(String orderId, int startIndex) {
        BeaconListResponse response = apiService.getBeacons(startIndex);

        List<TagBeaconDevice> beacons = Lists.newArrayList();

        if (response.getSearchMeta().getNextResults() != null && !response.getSearchMeta().getNextResults().toString().trim().equals("")) {
            beacons.addAll(fetchBeaconsFromManufacturer(orderId, startIndex + response.getSearchMeta().getMaxResult()));
        }

        beacons.addAll(response.getDevices().stream().filter(tagBeaconDevice -> tagBeaconDevice.getOrderId().equals(orderId))
                .collect(Collectors.toList()));

        return beacons;
    }

    @Override
    public Beacon update(String id, BeaconUpdate beaconUpdate) throws BeaconNotFoundException {
        Beacon beacon = find(id);
        TagBeaconConfig tagBeaconConfig = TagBeaconConfig.fromBeaconUpdate(beaconUpdate, beacon);

        if (isNewConfig(tagBeaconConfig, beacon)) {
            CompletableFuture<ResponseEntity<List<BeaconConfigResponse>>> configResponse = createConfig(tagBeaconConfig);
            CompletableFuture.allOf(configResponse).join();

            try {
                if (configResponse.get().getStatusCode() != HttpStatus.CREATED) {
                    throw new BeaconConfigurationNotCreatedException();
                }

                beacon = find(id);
                beacon.applyBeaconData(beaconDataService.update(id, beaconUpdate));

                return beacon;
            } catch (InterruptedException | ExecutionException e) {
                throw new BeaconConfigurationNotCreatedException();
            }

        } else {
            CompletableFuture<ResponseEntity<BeaconConfigDeletionResponse>> configResponse = deleteConfig(beacon);
            CompletableFuture.allOf(configResponse).join();

            try {
                if (configResponse.get().getStatusCode() != HttpStatus.OK) {
                    throw new BeaconConfigurationNotDeletedException();
                }

                beacon = find(id);

                beacon.applyBeaconData(beaconDataService.update(id, beaconUpdate));

                return beacon;
            } catch (InterruptedException | ExecutionException e) {
                throw new BeaconConfigurationNotCreatedException();
            }
        }
    }

    @Override
    public Beacon updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate) throws BeaconNotFoundException {
        Beacon beacon = find(id);
        beacon.applyBeaconData(beaconDataService.updateBatteryLevel(id, batteryLevelUpdate));

        return beacon;
    }

    private boolean isNewConfig(TagBeaconConfig tagBeaconConfig, Beacon beacon) {
        return !tagBeaconConfig.getProximity().equals(beacon.getUuid())
                || tagBeaconConfig.getMajor() != beacon.getMajor()
                || tagBeaconConfig.getMinor() != beacon.getMinor()
                || tagBeaconConfig.getInterval() != beacon.getInterval()
                || tagBeaconConfig.getTxPower() != beacon.getTxPower()
                || (tagBeaconConfig.getPackets().contains(Packet.IBEACON) && tagBeaconConfig.getProfiles().contains(Profile.IBEACON)) != beacon.isiBeacon()
                || (tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_UID) && tagBeaconConfig.getProfiles().contains(Profile.EDDYSTONE)) != beacon.isEddystoneUid()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_URL) != beacon.isEddystoneUrl()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_TLM) != beacon.isEddystoneTlm()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_EID) != beacon.isEddystoneEid()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_ETLM) != beacon.isEddystoneEtlm();
    }

    @Async
    private CompletableFuture<ResponseEntity<List<BeaconConfigResponse>>> createConfig(TagBeaconConfig tagBeaconConfig) {
        return CompletableFuture.completedFuture(apiService.createConfig(tagBeaconConfig));
    }

    @Async
    private CompletableFuture<ResponseEntity<BeaconConfigDeletionResponse>> deleteConfig(Beacon beacon) {
        return CompletableFuture.completedFuture(apiService.deleteConfig(beacon.getManufacturerId()));
    }

    @Override
    public ResponseEntity<?> delete(String id) throws BeaconNotFoundException {
        return null;
    }

    private Map<String, RemoteBeacon> getRemoteBeacons(List<BeaconData> beaconDatas) {
        Map<String, RemoteBeacon> remoteBeaconMap = Maps.newHashMap();

        List<CompletableFuture<BeaconListResponse>> completableFutures = Lists.newArrayList();

        Lists.partition(beaconDatas.stream().map(BeaconData::getManufacturerId)
                .collect(Collectors.toList()), 200).forEach(block -> {
                    completableFutures.add(CompletableFuture.completedFuture(apiService.getBeacons(block)));
        });

        CompletableFuture<Void> allFutures =
                CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<BeaconListResponse>> allCompletableFuture = allFutures
                .thenApply(future -> completableFutures.stream().map(CompletableFuture::join)
                    .collect(Collectors.toList())
        );

        allCompletableFuture.thenApply(beaconListResponses -> {
           beaconListResponses.forEach(beaconListResponse -> {
               remoteBeaconMap.putAll(getBeaconsWithStatuses(beaconListResponse));
           });
           return null;
        });

        return remoteBeaconMap;
    }

    private Map<String, RemoteBeacon> getBeaconsWithStatuses(BeaconListResponse response) {
        if (response == null || response.getDevices() == null) {
            return Maps.newHashMap();
        }

        Map<String, RemoteBeacon> remoteBeacons = response.getDevices().stream()
                .filter(tagBeaconDevice -> ManufacturerNameValidator.isValid(tagBeaconDevice.getName()))
                .map(RemoteBeacon::fromTagBeaconDevice)
                .collect(Collectors.toMap(RemoteBeacon::getManufacturerId, Function.identity()));

        CompletableFuture<DeviceStatusListResponse> statusListResponseFuture = CompletableFuture
                .completedFuture(apiService.getDeviceStatuses(new ArrayList<>(remoteBeacons.keySet())));
        CompletableFuture<ConfigurationListResponse> configListResponseFuture = CompletableFuture
                .completedFuture(apiService.getConfigurations(new ArrayList<>(remoteBeacons.keySet())));

        CompletableFuture.allOf(statusListResponseFuture, configListResponseFuture).join();


        try {
            DeviceStatusListResponse statusListResponse = statusListResponseFuture.get();
            ConfigurationListResponse configListResponse = configListResponseFuture.get();

            if (statusListResponse != null && statusListResponse.getStatuses() != null) {
                statusListResponse.getStatuses().forEach(status -> {
                    RemoteBeacon remoteBeacon = remoteBeacons.get(status.getUniqueId());
                    if (remoteBeacon != null) {
                        remoteBeacon.setBatteryLevel(status.getBatteryLevel());
                        remoteBeacon.setLastSeen(status.getLastEventTimestamp());
                    }
                });
            }

            if (configListResponse != null && configListResponse.getConfigs() != null) {
                configListResponse.getConfigs().forEach(configuration -> {
                    RemoteBeacon remoteBeacon = remoteBeacons.get(configuration.getUniqueId());
                    if (remoteBeacon != null) {
                        remoteBeacon.setPendingConfiguration(PendingConfiguration.fromBeaconConfiguration(configuration, remoteBeacon));
                    }
                });
            }
        } catch (InterruptedException | ExecutionException e) {

        }
        return remoteBeacons;
    }
}