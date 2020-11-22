package it.bz.beacon.api.scheduledtask.remote;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.cache.remote.RemoteBeaconCache;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.repository.BeaconDataRepository;
import it.bz.beacon.api.db.repository.GroupRepository;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.kontakt.io.ApiService;
import it.bz.beacon.api.kontakt.io.model.TagBeaconDevice;
import it.bz.beacon.api.kontakt.io.response.BeaconListResponse;
import it.bz.beacon.api.kontakt.io.response.ConfigurationListResponse;
import it.bz.beacon.api.kontakt.io.response.DeviceStatusListResponse;
import it.bz.beacon.api.model.PendingConfiguration;
import it.bz.beacon.api.model.RemoteBeacon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Service
public class RemoteBeaconCacheTask {

    @Autowired
    private BeaconDataRepository beaconDataRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private RemoteBeaconCache remoteBeaconCache;

    @Scheduled(fixedDelay = 60 * 1000)
    public void updateCache() {
        groupRepository.findAll().stream().filter(group -> group.getKontaktIoApiKey() != null && !group.getKontaktIoApiKey().isEmpty())
                .forEach(group -> {
                    Lists.partition(beaconDataRepository.findAllByGroupId(group.getId())
                            .stream().map(BeaconData::getManufacturerId).collect(Collectors.toList()), 200)
                            .forEach(block -> {
                                apiService.setApiKey(group.getKontaktIoApiKey());
                                BeaconListResponse beacons = apiService.getBeacons(block);
                                Map<String, RemoteBeacon> beaconsWithStatuses = getBeaconsWithStatuses(beacons);
                                remoteBeaconCache.addAll(beaconsWithStatuses);
                                updateBeaconPackageData(beaconsWithStatuses);
                            });
                });
    }


    private void updateBeaconPackageData(Map<String, RemoteBeacon> beaconsWithStatuses) {
        for (RemoteBeacon remoteBeacon : beaconsWithStatuses.values()) {
            try {
                BeaconData beaconData = beaconDataRepository.findById(remoteBeacon.getId()).orElseThrow(BeaconNotFoundException::new);
                beaconData.setUuid(remoteBeacon.getUuid());
                beaconData.setMajor(remoteBeacon.getMajor());
                beaconData.setMinor(remoteBeacon.getMinor());
                beaconData.setNamespace(remoteBeacon.getNamespace());
                beaconData.setInstanceId(remoteBeacon.getInstanceId());

                beaconData.setRemoteBeacon(remoteBeacon);
                beaconData.setRemoteBeaconUpdatedAt(new Date());

                beaconDataRepository.save(beaconData);
            } catch (InfoNotFoundException e) {
                e.printStackTrace();
                //TODO handle
            }
        }
    }

    private Map<String, RemoteBeacon> getBeaconsWithStatuses(BeaconListResponse response) {
        if (response == null || response.getDevices() == null || response.getDevices().size() == 0) {
            return Maps.newHashMap();
        }

        List<TagBeaconDevice> devices = response.getDevices();

        Map<String, RemoteBeacon> remoteBeacons = devices.stream()
                // .filter(tagBeaconDevice -> ManufacturerNameValidator.isValid(tagBeaconDevice.getName()))
                .map(RemoteBeacon::fromTagBeaconDevice).collect(Collectors.toMap(RemoteBeacon::getManufacturerId, Function.identity()));

        List<String> uniqueIds = Lists.newArrayList(remoteBeacons.keySet());

        CompletableFuture<DeviceStatusListResponse> statusListResponseFuture = CompletableFuture
                .completedFuture(apiService.getDeviceStatuses(uniqueIds));
        CompletableFuture<ConfigurationListResponse> configListResponseFuture = CompletableFuture
                .completedFuture(apiService.getConfigurations(uniqueIds));

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
            throw new RuntimeException(e);
        }
        return remoteBeacons.values().stream().collect(Collectors.toMap(RemoteBeacon::getId, Function.identity()));
    }
}