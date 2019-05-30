package it.bz.beacon.api.scheduledtask.remote;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.cache.remote.RemoteBeaconCache;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.repository.BeaconDataRepository;
import it.bz.beacon.api.kontakt.io.ApiService;
import it.bz.beacon.api.kontakt.io.response.BeaconListResponse;
import it.bz.beacon.api.kontakt.io.response.ConfigurationListResponse;
import it.bz.beacon.api.kontakt.io.response.DeviceStatusListResponse;
import it.bz.beacon.api.model.PendingConfiguration;
import it.bz.beacon.api.model.RemoteBeacon;
import it.bz.beacon.api.util.ManufacturerNameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Service
public class RemoteBeaconCacheTask {

    @Autowired
    private BeaconDataRepository beaconDataRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private RemoteBeaconCache remoteBeaconCache;

    @Scheduled(fixedDelay = 60 * 1000)
    public void updateCache() {
        Lists.partition(beaconDataRepository.findAll().stream().map(BeaconData::getManufacturerId)
                .collect(Collectors.toList()), 200).forEach(block -> {
            remoteBeaconCache.addAll(getBeaconsWithStatuses(apiService.getBeacons(block)));
        });
    }

    private Map<String, RemoteBeacon> getBeaconsWithStatuses(BeaconListResponse response) {
        if (response == null || response.getDevices() == null) {
            return Maps.newHashMap();
        }

        Map<String, RemoteBeacon> remoteBeacons = response.getDevices().stream()
                .filter(tagBeaconDevice -> ManufacturerNameValidator.isValid(tagBeaconDevice.getName()))
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

        }
        return remoteBeacons.values().stream().collect(Collectors.toMap(RemoteBeacon::getId, Function.identity()));
    }
}