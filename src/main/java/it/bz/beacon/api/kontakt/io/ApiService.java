package it.bz.beacon.api.kontakt.io;

import it.bz.beacon.api.config.KontaktIOConfiguration;
import it.bz.beacon.api.exception.kontakt.io.InvalidApiKeyException;
import it.bz.beacon.api.kontakt.io.model.BeaconConfigDeletionResponse;
import it.bz.beacon.api.kontakt.io.model.BeaconConfigResponse;
import it.bz.beacon.api.kontakt.io.model.Device;
import it.bz.beacon.api.kontakt.io.model.TagBeaconConfig;
import it.bz.beacon.api.kontakt.io.response.AssignOrderResponse;
import it.bz.beacon.api.kontakt.io.response.BeaconListResponse;
import it.bz.beacon.api.kontakt.io.response.ConfigurationListResponse;
import it.bz.beacon.api.kontakt.io.response.DeviceStatusListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    private String apiKey;

    @Autowired
    private HttpHeaders httpHeaders;

    @Autowired
    private KontaktIOConfiguration kontaktIOConfiguration;

    public ApiService() {
    }


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public HttpHeaders getHttpHeaders() {
        if (apiKey == null)
            return httpHeaders;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", kontaktIOConfiguration.getAcceptHeader());
        headers.set("Api-Key", apiKey);

        return headers;
    }

    public BeaconListResponse getBeacons() {
        ResponseEntity<BeaconListResponse> responseEntity = restTemplate.exchange(
                "/device?deviceType=" + Device.DeviceType.BEACON + "&maxResult=500",
                HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeaders()),
                new ParameterizedTypeReference<BeaconListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public BeaconListResponse getBeacons(int index) {
        ResponseEntity<BeaconListResponse> responseEntity = restTemplate.exchange(
                "/device?deviceType=" + Device.DeviceType.BEACON + "&maxResult=500&startIndex=" + index,
                HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeaders()),
                new ParameterizedTypeReference<BeaconListResponse>() {
                }
        );

        return responseEntity.getBody();
    }

    public BeaconListResponse getBeacons(List<String> ids) {

        if (ids.size() <= 0) {
            return new BeaconListResponse();
        }

        try {
            ResponseEntity<BeaconListResponse> responseEntity = restTemplate.exchange(
                    "/device?maxResult=" + ids.size() + "&deviceType=" + Device.DeviceType.BEACON + "&uniqueId=" + String.join(",", ids),
                    HttpMethod.GET,
                    new HttpEntity<>(null, getHttpHeaders()),
                    new ParameterizedTypeReference<BeaconListResponse>() {
                    }
            );
            return responseEntity.getBody();
        }
        catch (RuntimeException exxx)
        {
            throw new InvalidApiKeyException();
        }


    }

    public DeviceStatusListResponse getDeviceStatuses(List<String> ids) {
        if (ids.size() <= 0) {
            return new DeviceStatusListResponse();
        }

        ResponseEntity<DeviceStatusListResponse> responseEntity = restTemplate.exchange(
                "/device/status?maxResult=" + ids.size() + "&uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeaders()),
                new ParameterizedTypeReference<DeviceStatusListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public ConfigurationListResponse getConfigurations(List<String> ids) {
        if (ids.size() <= 0) {
            return new ConfigurationListResponse();
        }

        ResponseEntity<ConfigurationListResponse> responseEntity = restTemplate.exchange(
                "/config?maxResult=" + ids.size() + "&uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeaders()),
                new ParameterizedTypeReference<ConfigurationListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public ResponseEntity<List<BeaconConfigResponse>> createConfig(TagBeaconConfig config) {
        HttpHeaders headers = getHttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

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
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<List<BeaconConfigResponse>>() {}
        );
    }

    public ResponseEntity<BeaconConfigDeletionResponse> deleteConfig(String id) {
        HttpHeaders headers = getHttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("uniqueId", id);

        return restTemplate.exchange(
                "/config/delete",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<BeaconConfigDeletionResponse>() {}
        );
    }

    public List<String> checkOrder(String orderId) {
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(
                "/order?orderId=" + orderId,
                HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeaders()),
                new ParameterizedTypeReference<List<String>>() {}
        );
        return responseEntity.getBody();
    }

    public AssignOrderResponse assignOrder(String orderId) {
        HttpHeaders headers = getHttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("orderId", orderId);

        ResponseEntity<AssignOrderResponse> responseEntity = restTemplate.exchange(
                "/order/assign",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<AssignOrderResponse>() {
                }
        );
        return responseEntity.getBody();
    }
}
