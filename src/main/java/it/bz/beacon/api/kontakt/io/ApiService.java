package it.bz.beacon.api.kontakt.io;

import com.google.common.collect.Lists;
import it.bz.beacon.api.kontakt.io.model.Device;
import it.bz.beacon.api.kontakt.io.model.TagBeaconConfig;
import it.bz.beacon.api.kontakt.io.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders httpHeaders;

    public BeaconListResponse getBeacons() {
        ResponseEntity<BeaconListResponse> responseEntity = restTemplate.exchange(
                "/device?deviceType=" + Device.DeviceType.BEACON,
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<BeaconListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public BeaconListResponse getBeacons(List<String> ids) {
        //TODO set maxResults
        ResponseEntity<BeaconListResponse> responseEntity = restTemplate.exchange(
                "/device?maxResults=7000&deviceType=" + Device.DeviceType.BEACON + "&uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<BeaconListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public DeviceStatusListResponse getDeviceStatuses(List<String> ids) {
        //TODO set maxResults
        ResponseEntity<DeviceStatusListResponse> responseEntity = restTemplate.exchange(
                "/device/status?uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<DeviceStatusListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public ConfigurationListResponse getConfigurations(List<String> ids) {
        //TODO set maxResults
        ResponseEntity<ConfigurationListResponse> responseEntity = restTemplate.exchange(
                "/config?uniqueId=" + String.join(",", ids),
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<ConfigurationListResponse>() {}
        );

        return responseEntity.getBody();
    }

    public ResponseEntity<DefaultResponse> createConfig(List<TagBeaconConfig> config) {
        return restTemplate.exchange(
                "/config/create",
                HttpMethod.POST,
                new HttpEntity<>(config, httpHeaders),
                new ParameterizedTypeReference<DefaultResponse>() {}
        );
    }

    public OrderCheckResponse checkOrder(String orderId) {
        ResponseEntity<OrderCheckResponse> responseEntity = restTemplate.exchange(
                "/order?orderId=" + orderId,
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                new ParameterizedTypeReference<OrderCheckResponse>() {
                }
                );
        return responseEntity.getBody();
    }

    public AssignOrderResponse assignOrder(String orderId) {
        ResponseEntity<AssignOrderResponse> responseEntity = restTemplate.exchange(
                "/order?orderId=" + orderId,
                HttpMethod.POST,
                new HttpEntity<>(Lists.newArrayList(orderId), httpHeaders),
                new ParameterizedTypeReference<AssignOrderResponse>() {
                }
        );
        return responseEntity.getBody();
    }
}
