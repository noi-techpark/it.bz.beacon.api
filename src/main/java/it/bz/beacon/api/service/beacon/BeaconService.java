package it.bz.beacon.api.service.beacon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.cache.remote.RemoteBeaconCache;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.model.UserRoleGroup;
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

    @Override
    public List<Beacon> findAll() {
        List<BeaconData> beaconDatas = beaconDataService.findAll();
        Map<String, RemoteBeacon> remoteBeacons = getRemoteBeacons(beaconDatas);

        return beaconDatas.stream()
                .map(beaconData -> Beacon.fromRemoteBeacon(beaconData, remoteBeacons.get(beaconData.getManufacturerId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Beacon> findAllWithRemoteCache() {
        return findAllWithRemoteCache(null);
    }

    @Override
    public List<Beacon> findAllWithRemoteCache(Long groupId) {
        List<BeaconData> beaconDatas = groupId != null ? beaconDataService.findAllByGroupId(groupId) : beaconDataService.findAll();

        List<Issue> issues = issueRepository.findAll();

        Map<String, List<Issue>> issueMap = new HashMap<>();

        issues.forEach(issue -> {
            if (!issueMap.containsKey(issue.getBeaconData().getId())) {
                issueMap.put(issue.getBeaconData().getId(), Lists.newArrayList());
            }

            issueMap.get(issue.getBeaconData().getId()).add(issue);
        });

        beaconDatas.forEach(beaconData -> {
            beaconData.setIssues(issueMap.get(beaconData.getId()));
        });

        List<Beacon> result = beaconDatas.stream()
                .map(beaconData -> Beacon.fromRemoteBeacon(beaconData, remoteBeaconCache.get(beaconData.getId())))
                .collect(Collectors.toList());

        return result;
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

        RemoteBeacon remoteBeacon = remoteBeacons.get(beaconData.getManufacturerId());
        if (remoteBeacon != null) // remoteBeacon is null if not present in kontakt.io
           remoteBeaconCache.add(getBeaconWithStatus(remoteBeacon));

        return Beacon.fromRemoteBeacon(beaconData, remoteBeacon);
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

                    beaconData = beaconDataService.create(createBeaconData);

                    remoteBeaconCache.add(remoteBeacon);
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
        boolean auth = false;

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authorizedUser.isAdmin())
            auth = true;
        else
            for (UserRoleGroup userRoleGroup : authorizedUser.getGroups()) {
                if (beaconUpdate.getGroup() == userRoleGroup.getGroup().getId() &&
                        (userRoleGroup.getRole() == UserRole.MANAGER || userRoleGroup.getRole() == UserRole.BEACON_EDITOR)) {
                    auth = true;

                    break;
                }
            }

        if (!auth)
            throw new InsufficientRightsException();

        Beacon beacon = find(id);
        if (!authorizedUser.isAdmin() && (beaconUpdate.getGroup() == null && beacon.getGroup() == null
                || beaconUpdate.getGroup() == null || !beaconUpdate.getGroup().equals(beacon.getGroup())))
            throw new InsufficientRightsException();

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

                BeaconData beaconData = beaconDataService.update(id, beaconUpdate);
                beacon.applyBeaconData(beaconData);

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

        List<CompletableFuture<Map<String, RemoteBeacon>>> completableFutures = Lists.newArrayList();

        Lists.partition(beaconDatas.stream().map(BeaconData::getManufacturerId)
                .collect(Collectors.toList()), 200).forEach(block -> {
                    completableFutures.add(CompletableFuture.completedFuture(getBeaconsWithStatuses(apiService.getBeacons(block))));
        });

        CompletableFuture<Void> allFutures =
                CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));

        CompletableFuture<List<Map<String, RemoteBeacon>>> allCompletableFuture = allFutures
                .thenApply(future -> completableFutures.stream().map(CompletableFuture::join)
                    .collect(Collectors.toList())
        );

        allCompletableFuture.thenApply(maps -> {
            maps.forEach(map -> remoteBeaconMap.putAll(map));
            return null;
        });

        return remoteBeaconMap;

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