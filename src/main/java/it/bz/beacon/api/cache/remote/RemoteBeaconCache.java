package it.bz.beacon.api.cache.remote;

import com.google.common.collect.Maps;
import it.bz.beacon.api.model.RemoteBeacon;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RemoteBeaconCache {

    // TODO is a synchronized map required for multithreading?
    private Map<String, RemoteBeacon> cache = Maps.newHashMap();

    public void add(RemoteBeacon remoteBeacon) {
        cache.put(remoteBeacon.getId(), remoteBeacon);
    }

    public void addAll(Map<String, RemoteBeacon> remoteBeacons) {
        cache.putAll(remoteBeacons);
    }

    public RemoteBeacon get(String id) {
        return cache.getOrDefault(id, null);
    }
}
