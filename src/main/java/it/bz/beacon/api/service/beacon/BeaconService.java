package it.bz.beacon.api.service.beacon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.bz.beacon.api.cache.remote.RemoteBeaconCache;
import it.bz.beacon.api.db.model.*;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.db.*;
import it.bz.beacon.api.exception.kontakt.io.InvalidApiKeyException;
import it.bz.beacon.api.exception.order.NoBeaconsToOrderException;
import it.bz.beacon.api.exception.order.NoGroupToOrderException;
import it.bz.beacon.api.kontakt.io.ApiService;
import it.bz.beacon.api.kontakt.io.model.*;
import it.bz.beacon.api.kontakt.io.model.enumeration.Packet;
import it.bz.beacon.api.kontakt.io.model.enumeration.Profile;
import it.bz.beacon.api.kontakt.io.response.BeaconListResponse;
import it.bz.beacon.api.kontakt.io.response.ConfigurationListResponse;
import it.bz.beacon.api.kontakt.io.response.DeviceStatusListResponse;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.model.enumeration.UserRole;
import it.bz.beacon.api.service.group.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private IBeaconDataService beaconDataService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private RemoteBeaconCache remoteBeaconCache;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public List<Beacon> findAll() {
        return beaconDataService.findAllBeacon();
    }

    @Override
    public List<Beacon> findAllByGroupId(Long groupId) {
        return beaconDataService.findAllBeaconByGroupId(groupId);
    }

    @Override
    public List<Beacon> findAllWithIds(List<String> ids) {
        if (ids.isEmpty()) {
            return Lists.newArrayList();
        }
        return beaconDataService.findAllBeacon(ids);
    }

    @Override
    public Beacon find(String id) {
        ApiService apiService = applicationContext.getBean(ApiService.class);
        return find(apiService, id);
    }

    public Beacon find(ApiService apiService, String id) {
        BeaconData beaconData = beaconDataService.find(id);

        apiService.setApiKey(beaconData.getGroup().getKontaktIoApiKey());
        RemoteBeacon remoteBeacon = findRemoteBeacon(apiService, beaconData.getManufacturerId());
        if (remoteBeacon != null && !remoteBeacon.equals(beaconData.getRemoteBeacon())) {
            beaconData.setRemoteBeacon(remoteBeacon);
            beaconData.setRemoteBeaconUpdatedAt(new Date());
            Beacon ret = beaconDataService.update(beaconData);
            return ret;
        }

        return beaconDataService.findBeacon(id).orElseThrow(BeaconNotFoundException::new);
    }

    private RemoteBeacon findRemoteBeacon(ApiService apiService, String manufacturerId) {
        List<String> block = Lists.newArrayList(manufacturerId);
        Map<String, RemoteBeacon> remoteBeacons = getBeaconsWithStatuses(apiService, apiService.getBeacons(block));
        return remoteBeacons.get(manufacturerId);
    }

    @Override
    public List<Beacon> createByOrder(ManufacturerOrder order) throws NoGroupToOrderException,
            InsufficientRightsException, BeaconNotFoundException, NoBeaconsToOrderException {
        boolean auth = false;

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Group group;
        try {
            group = groupService.find(order.getGroupId());
        } catch (GroupNotFoundException e) {
            throw new NoGroupToOrderException();
        }
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

        ApiService apiService = applicationContext.getBean(ApiService.class);
        apiService.setApiKey(group.getKontaktIoApiKey());
        try {
            apiService.assignOrder(order.getId());
        } catch (HttpClientErrorException e) {
            // if order was already used is not possible to assign order a second time
            // order can be assigned from the kontakt.io web interface too.
            // throw e;
        }

        List<TagBeaconDevice> orderBeacons = fetchBeaconsFromManufacturer(apiService, order.getId());

        List<Beacon> orderBeaconsResult = orderBeacons.stream().map(tagBeaconDevice -> {
            try {
                RemoteBeacon remoteBeacon = getBeaconWithStatus(apiService, RemoteBeacon.fromTagBeaconDevice(tagBeaconDevice));
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

                createBeaconData.setGroup(groupService.find(order.getGroupId()));

                createBeaconData.setRemoteBeacon(remoteBeacon);
                createBeaconData.setRemoteBeaconUpdatedAt(new Date());

                return beaconDataService.create(createBeaconData);
            } catch (InvalidBeaconIdentifierException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (orderBeaconsResult.isEmpty())
            throw new NoBeaconsToOrderException();

        return orderBeaconsResult;
    }

    private List<TagBeaconDevice> fetchBeaconsFromManufacturer(ApiService apiService, String orderId) {
        return fetchBeaconsFromManufacturer(apiService, orderId, 0);
    }

    private List<TagBeaconDevice> fetchBeaconsFromManufacturer(ApiService apiService, String orderId, int startIndex) {
        BeaconListResponse response = apiService.getBeacons(startIndex);

        List<TagBeaconDevice> beacons = Lists.newArrayList();

        if (response.getSearchMeta().getNextResults() != null && !response.getSearchMeta().getNextResults().toString().trim().equals("")) {
            beacons.addAll(fetchBeaconsFromManufacturer(apiService, orderId, startIndex + response.getSearchMeta().getMaxResult()));
        }

        beacons.addAll(response.getDevices().stream().filter(tagBeaconDevice -> tagBeaconDevice.getOrderId().equals(orderId))
                .collect(Collectors.toList()));

        return beacons;
    }

    @Override
    public Beacon update(String id, BeaconUpdate beaconUpdate) throws BeaconNotFoundException, InvalidApiKeyException {
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

        BeaconData beaconData = beaconDataService.find(id);

        ApiService apiService = applicationContext.getBean(ApiService.class);
        apiService.setApiKey(beaconData.getGroup().getKontaktIoApiKey());

        if (!Objects.equals(beaconUpdate.getGroup(), beaconData.getGroup() == null ? null : beaconData.getGroup().getId())) {
            if (!authorizedUser.isAdmin()) {
                throw new InsufficientRightsException();
            }
            if (beaconUpdate.getGroup() != null) {
                Group newGroup = groupService.find(beaconUpdate.getGroup());
                if (!Objects.equals(newGroup.getKontaktIoApiKey(), beaconData.getGroup() != null ?
                        beaconData.getGroup().getKontaktIoApiKey() : null)) {

                    apiService.setApiKey(newGroup.getKontaktIoApiKey());
                    if (apiService.getBeacons(Lists.newArrayList(beaconData.getManufacturerId())).getDevices().stream()
                            .noneMatch(tagBeaconDevice ->
                                    tagBeaconDevice.getUniqueId().equals(beaconData.getManufacturerId())
                                            && (tagBeaconDevice.getAccess() == Device.Access.OWNER
                                            || tagBeaconDevice.getAccess() == Device.Access.SUPERVISOR
                                            || tagBeaconDevice.getAccess() == Device.Access.EDITOR)
                            ))
                        throw new InvalidApiKeyException();
                }
            }
        }

        TagBeaconConfig tagBeaconConfig = TagBeaconConfig.fromBeaconUpdate(beaconUpdate, beaconData);
        RemoteBeacon remoteBeacon = findRemoteBeacon(apiService, beaconData.getManufacturerId());

        if (isNewConfig(tagBeaconConfig, remoteBeacon)) {
            if (isNewPendingConfig(tagBeaconConfig, remoteBeacon.getPendingConfiguration())) {
                CompletableFuture<ResponseEntity<List<BeaconConfigResponse>>> configResponse = createConfig(apiService, tagBeaconConfig);
                CompletableFuture.allOf(configResponse).join();

                try {
                    if (configResponse.get().getStatusCode() != HttpStatus.CREATED) {
                        throw new BeaconConfigurationNotCreatedException();
                    }
                    remoteBeacon = findRemoteBeacon(apiService, beaconData.getManufacturerId());
                } catch (InterruptedException | ExecutionException e) {
                    throw new BeaconConfigurationNotCreatedException();
                }
            }

        } else {
            if (remoteBeacon.getPendingConfiguration() != null) {
                CompletableFuture<ResponseEntity<BeaconConfigDeletionResponse>> configResponse = deleteConfig(apiService, beaconData);
                CompletableFuture.allOf(configResponse).join();

                try {
                    if (configResponse.get().getStatusCode() != HttpStatus.OK) {
                        throw new BeaconConfigurationNotDeletedException();
                    }
                    remoteBeacon = findRemoteBeacon(apiService, beaconData.getManufacturerId());
                } catch (InterruptedException | ExecutionException e) {
                    throw new BeaconConfigurationNotCreatedException();
                }
            }
        }

        return beaconDataService.update(id, beaconUpdate, remoteBeacon);
    }

    @Override
    public Beacon updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate) {
        beaconDataService.updateBatteryLevel(id, batteryLevelUpdate);
        return beaconDataService.updateBatteryLevel(id, batteryLevelUpdate);
    }

    private boolean isNewConfig(TagBeaconConfig tagBeaconConfig, RemoteBeacon remoteBeacon) {
        return !tagBeaconConfig.getProximity().equals(remoteBeacon.getUuid())
                || !tagBeaconConfig.getMajor().equals(remoteBeacon.getMajor())
                || !tagBeaconConfig.getMinor().equals(remoteBeacon.getMinor())
                || !tagBeaconConfig.getInterval().equals(remoteBeacon.getInterval())
                || !tagBeaconConfig.getTxPower().equals(remoteBeacon.getTxPower())
                || (tagBeaconConfig.getPackets().contains(Packet.IBEACON) && tagBeaconConfig.getProfiles().contains(Profile.IBEACON)) != remoteBeacon.isiBeacon()
                || (tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_UID) && tagBeaconConfig.getProfiles().contains(Profile.EDDYSTONE)) != remoteBeacon.isEddystoneUid()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_URL) != remoteBeacon.isEddystoneUrl()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_TLM) != remoteBeacon.isEddystoneTlm()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_EID) != remoteBeacon.isEddystoneEid()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_ETLM) != remoteBeacon.isEddystoneEtlm();
    }

    private boolean isNewPendingConfig(TagBeaconConfig tagBeaconConfig, PendingConfiguration pendingConfiguration) {
        return pendingConfiguration == null
                || !tagBeaconConfig.getProximity().equals(pendingConfiguration.getUuid())
                || !tagBeaconConfig.getMajor().equals(pendingConfiguration.getMajor())
                || !tagBeaconConfig.getMinor().equals(pendingConfiguration.getMinor())
                || !tagBeaconConfig.getInterval().equals(pendingConfiguration.getInterval())
                || !tagBeaconConfig.getTxPower().equals(pendingConfiguration.getTxPower())
                || (tagBeaconConfig.getPackets().contains(Packet.IBEACON) && tagBeaconConfig.getProfiles().contains(Profile.IBEACON)) != pendingConfiguration.isiBeacon()
                || (tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_UID) && tagBeaconConfig.getProfiles().contains(Profile.EDDYSTONE)) != pendingConfiguration.isEddystoneUid()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_URL) != pendingConfiguration.isEddystoneUrl()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_TLM) != pendingConfiguration.isEddystoneTlm()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_EID) != pendingConfiguration.isEddystoneEid()
                || tagBeaconConfig.getPackets().contains(Packet.EDDYSTONE_ETLM) != pendingConfiguration.isEddystoneEtlm();
    }

    @Async
    private CompletableFuture<ResponseEntity<List<BeaconConfigResponse>>> createConfig(ApiService apiService, TagBeaconConfig tagBeaconConfig) {
        return CompletableFuture.completedFuture(apiService.createConfig(tagBeaconConfig));
    }

    @Async
    private CompletableFuture<ResponseEntity<BeaconConfigDeletionResponse>> deleteConfig(ApiService apiService, BeaconData beaconData) {
        return CompletableFuture.completedFuture(apiService.deleteConfig(beaconData.getManufacturerId()));
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return null;
    }

    private RemoteBeacon getBeaconWithStatus(ApiService apiService, RemoteBeacon remoteBeacon) {

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

    private Map<String, RemoteBeacon> getBeaconsWithStatuses(ApiService apiService, BeaconListResponse response) {
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