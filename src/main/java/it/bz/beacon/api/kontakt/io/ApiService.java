package it.bz.beacon.api.kontakt.io;

import it.bz.beacon.api.kontakt.io.model.BeaconConfigDeletionResponse;
import it.bz.beacon.api.kontakt.io.model.BeaconConfigResponse;
import it.bz.beacon.api.kontakt.io.model.Device;
import it.bz.beacon.api.kontakt.io.model.TagBeaconConfig;
import it.bz.beacon.api.kontakt.io.response.AssignOrderResponse;
import it.bz.beacon.api.kontakt.io.response.BeaconListResponse;
import it.bz.beacon.api.kontakt.io.response.ConfigurationListResponse;
import it.bz.beacon.api.kontakt.io.response.DeviceStatusListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders httpHeaders;

    public BeaconListResponse getBeacons() {
        ResponseEntity<BeaconListResponse> responseEntity = restTemplate.exchange(
                "/device?deviceType=" + Device.DeviceType.BEACON + "&maxResult=10000",
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<BeaconListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public BeaconListResponse getBeacons(List<String> ids) {
        ResponseEntity<BeaconListResponse> responseEntity = restTemplate.exchange(
                "/device?maxResults=" + ids.size() + "&deviceType=" + Device.DeviceType.BEACON + "&uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<BeaconListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public DeviceStatusListResponse getDeviceStatuses(List<String> ids) {
        ResponseEntity<DeviceStatusListResponse> responseEntity = restTemplate.exchange(
                "/device/status?maxResults=" + ids.size() + "&uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<DeviceStatusListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public ConfigurationListResponse getConfigurations(List<String> ids) {
        ResponseEntity<ConfigurationListResponse> responseEntity = restTemplate.exchange(
                "/config?maxResults=" + ids.size() + "&uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<ConfigurationListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public ResponseEntity<List<BeaconConfigResponse>> createConfig(TagBeaconConfig config) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("uniqueId", config.getUniqueId());
        requestBody.add("deviceType", Device.DeviceType.BEACON.toString());
        requestBody.add("profiles", config.getProfiles().stream().map(profile -> profile.toString()).collect(Collectors.joining(",")));
        requestBody.add("packets", config.getPackets().stream().map(packet -> packet.toString()).collect(Collectors.joining(",")));
        requestBody.add("proximity", config.getProximity().toString());
        requestBody.add("major", config.getMajor().toString());
        requestBody.add("minor", config.getMinor().toString());
        requestBody.add("namespace", config.getNamespace());
        requestBody.add("instanceId", config.getInstanceId());
        requestBody.add("url", config.getUrl());
        requestBody.add("interval", config.getInterval().toString());
        requestBody.add("txPower", config.getTxPower().toString());

        return restTemplate.exchange(
                "/config/create",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, httpHeaders),
                new ParameterizedTypeReference<List<BeaconConfigResponse>>() {}
        );
    }

    public ResponseEntity<BeaconConfigDeletionResponse> deleteConfig(String id) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("uniqueId", id);

        return restTemplate.exchange(
                "/config/delete",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, httpHeaders),
                new ParameterizedTypeReference<BeaconConfigDeletionResponse>() {}
        );
    }

    public List<String> checkOrder(String orderId) {
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                "/order?orderId=" + orderId,
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<List<String>>() {}
        );
        return responseEntity.getBody();
    }

    public AssignOrderResponse assignOrder(String orderId) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("orderId", orderId);

        ResponseEntity<AssignOrderResponse> responseEntity = restTemplate.exchange(
                "/order/assign",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, httpHeaders),
                new ParameterizedTypeReference<AssignOrderResponse>() {
                }
        );
        return responseEntity.getBody();
    }
}
