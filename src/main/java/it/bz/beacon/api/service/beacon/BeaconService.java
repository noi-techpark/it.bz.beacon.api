package it.bz.beacon.api.service.beacon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.cache.remote.RemoteBeaconCache;
import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.model.UserRoleGroup;
import it.bz.beacon.api.db.repository.BeaconRepository;
import it.bz.beacon.api.db.repository.GroupRepository;
import it.bz.beacon.api.db.repository.IssueRepository;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.db.BeaconConfigurationNotCreatedException;
import it.bz.beacon.api.exception.db.BeaconConfigurationNotDeletedException;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.exception.db.InvalidBeaconIdentifierException;
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
import it.bz.beacon.api.model.enumeration.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RemoteBeaconCache remoteBeaconCache;

    @Autowired
    private BeaconRepository beaconRepository;

    @Override
    public List<Beacon> findAll() {
        return beaconRepository.findAll();
    }

    @Override
    public List<Beacon> findAllByGroupId(Long groupId) {
        return beaconRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<Beacon> findAllWithIds(List<String> ids) {
        if (ids.isEmpty()) {
            return Lists.newArrayList();
        }
        return beaconRepository.findAllById(ids);
    }

    @Override
    public Beacon find(String id) {
        updateRemoteBeacon(id);
        return beaconRepository.findById(id).orElseThrow(BeaconNotFoundException::new);
    }

    @Override
    public List<Beacon> createByOrder(ManufacturerOrder order) {
        boolean auth = false;

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authorizedUser.isAdmin())
            auth = true;
        else
            for (UserRoleGroup userRoleGroup : authorizedUser.getGroups()) {
                if (order.getGroupId() != null && order.getGroupId() == userRoleGroup.getGroup().getId() &&
                        (userRoleGroup.getRole() == UserRole.MANAGER)) {
                    auth = true;

                    break;
                }
            }

        if (!auth)
            throw new InsufficientRightsException();


        try {
            apiService.assignOrder(order.getId());
        } catch (HttpClientErrorException e) {
            // if order was already used is not possible to assign order a second time
            // order can be assigned from the kontakt.io web interface too.
            // throw e;
        }

        List<TagBeaconDevice> orderBeacons = fetchBeaconsFromManufacturer(order.getId());

        return orderBeacons.stream().map(tagBeaconDevice -> {
                try {
                    RemoteBeacon remoteBeacon = getBeaconWithStatus(RemoteBeacon.fromTagBeaconDevice(tagBeaconDevice));
                    BeaconData beaconData;
                    try {
                        beaconData = beaconDataService.find(remoteBeacon.getId());
                    } catch (Exception e) {
                        beaconData = null;
                    }
                    if (beaconData != null) {
                        return null;
                    }

                    BeaconData createBeaconData = BeaconData.fromRemoteBeacon(remoteBeacon);

                    // group is optional
                    if (order.getGroupId() != null)
                       createBeaconData.setGroup(groupRepository.findById(order.getGroupId()).get());

                    beaconData.setRemoteBeacon(remoteBeacon);
                    beaconData.setRemoteBeaconUpdatedAt(new Date());

                    beaconData = beaconDataService.create(createBeaconData);

                    remoteBeaconCache.add(remoteBeacon);
                    return beaconRepository.findById(beaconData.getId()).orElseThrow(BeaconNotFoundException::new);
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

    private void updateRemoteBeacon(String id) {
        BeaconData beaconData = beaconDataService.find(id);
        List<String> block = Lists.newArrayList(beaconData.getManufacturerId());

        Map<String, RemoteBeacon> remoteBeacons = getBeaconsWithStatuses(apiService.getBeacons(block));

        if (remoteBeacons.containsKey(id) && !remoteBeacons.get(id).equals(beaconData.getRemoteBeacon())) {
            beaconData.setRemoteBeacon(remoteBeacons.get(id));
            beaconData.setRemoteBeaconUpdatedAt(new Date());
            beaconDataService.update(beaconData);
        }
    }

    @Override
    public Beacon update(String id, BeaconUpdate beaconUpdate) throws BeaconNotFoundException {
        boolean auth = false;

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authorizedUser.isAdmin())
            auth = true;
        else
            for (UserRoleGroup userRoleGroup : authorizedUser.getGroups()) {
                if (beaconUpdate.getGroup().equals(userRoleGroup.getGroup().getId()) &&
                        (userRoleGroup.getRole().equals(UserRole.MANAGER) || userRoleGroup.getRole().equals(UserRole.BEACON_EDITOR))) {
                    auth = true;

                    break;
                }
            }

        if (!auth)
            throw new InsufficientRightsException();

        Beacon beacon = find(id);
        if (!authorizedUser.isAdmin() && (beaconUpdate.getGroup() == null && beacon.getGroup() == null
                || beaconUpdate.getGroup() == null || !beaconUpdate.getGroup().equals(beacon.getGroup().getId())))
            throw new InsufficientRightsException();

        TagBeaconConfig tagBeaconConfig = TagBeaconConfig.fromBeaconUpdate(beaconUpdate, beacon);

        if (isNewConfig(tagBeaconConfig, beacon)) {
            CompletableFuture<ResponseEntity<List<BeaconConfigResponse>>> configResponse = createConfig(tagBeaconConfig);
            CompletableFuture.allOf(configResponse).join();

            try {
                if (configResponse.get().getStatusCode() != HttpStatus.CREATED) {
                    throw new BeaconConfigurationNotCreatedException();
                }

                beaconDataService.update(id, beaconUpdate);

                return beaconRepository.findById(id).orElseThrow(BeaconNotFoundException::new);
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

                beaconDataService.update(id, beaconUpdate);

                return beaconRepository.findById(id).orElseThrow(BeaconNotFoundException::new);
            } catch (InterruptedException | ExecutionException e) {
                throw new BeaconConfigurationNotCreatedException();
            }
        }
    }

    @Override
    public Beacon updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate) throws BeaconNotFoundException {
        beaconDataService.updateBatteryLevel(id, batteryLevelUpdate);

        return beaconRepository.findById(id).orElseThrow(BeaconNotFoundException::new);
    }

    private boolean isNewConfig(TagBeaconConfig tagBeaconConfig, Beacon beacon) {
        return !tagBeaconConfig.getProximity().equals(beacon.getUuid())
                || !tagBeaconConfig.getMajor().equals(beacon.getMajor())
                || !tagBeaconConfig.getMinor().equals(beacon.getMinor())
                || !tagBeaconConfig.getInterval().equals(beacon.getInterval())
                || !tagBeaconConfig.getTxPower().equals(beacon.getTxPower())
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

    private RemoteBeacon getBeaconWithStatus(RemoteBeacon remoteBeacon) {

        CompletableFuture<DeviceStatusListResponse> statusListResponseFuture = CompletableFuture
                .completedFuture(apiService.getDeviceStatuses(Lists.newArrayList(remoteBeacon.getManufacturerId())));
        CompletableFuture<ConfigurationListResponse> configListResponseFuture = CompletableFuture
                .completedFuture(apiService.getConfigurations(Lists.newArrayList(remoteBeacon.getManufacturerId())));

        CompletableFuture.allOf(statusListResponseFuture, configListResponseFuture).join();


        try {
            DeviceStatusListResponse statusListResponse = statusListResponseFuture.get();
            ConfigurationListResponse configListResponse = configListResponseFuture.get();

            if (statusListResponse != null && statusListResponse.getStatuses() != null) {
                statusListResponse.getStatuses().forEach(status -> {
                    if (remoteBeacon != null) {
                        remoteBeacon.setBatteryLevel(status.getBatteryLevel());
                        remoteBeacon.setLastSeen(status.getLastEventTimestamp());
                    }
                });
            }

            if (configListResponse != null && configListResponse.getConfigs() != null) {
                configListResponse.getConfigs().forEach(configuration -> {
                    if (remoteBeacon != null) {
                        remoteBeacon.setPendingConfiguration(PendingConfiguration.fromBeaconConfiguration(configuration, remoteBeacon));
                    }
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return remoteBeacon;
    }

    private Map<String, RemoteBeacon> getBeaconsWithStatuses(BeaconListResponse response) {
        if (response == null || response.getDevices() == null || response.getDevices().size() == 0) {
            return Maps.newHashMap();
        }

        Map<String, RemoteBeacon> remoteBeacons = response.getDevices().stream()
                // .filter(tagBeaconDevice -> ManufacturerNameValidator.isValid(tagBeaconDevice.getName()))
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
            throw new RuntimeException(e);
        }
        return remoteBeacons;
    }
}