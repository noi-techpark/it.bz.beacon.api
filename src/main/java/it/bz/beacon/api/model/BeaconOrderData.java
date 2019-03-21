package it.bz.beacon.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.db.model.OrderData;

import java.util.UUID;

public class BeaconOrderData {
    @JsonIgnore
    private String orderSymbol;
    private String beaconId;
    private UUID uuid;
    private int major;
    private int minor;
    private String namespace;
    private String instanceId;
    private int zoneId;
    private String zoneCode;

    public BeaconOrderData(OrderData orderData) {
        this.orderSymbol = orderData.getOrderSymbol();
        this.beaconId = orderData.getId();
        this.uuid = orderData.getInfo().getUuid();
        this.major = orderData.getInfo().getMajor();
        this.minor = orderData.getInfo().getMinor();
        this.namespace = orderData.getInfo().getNamespace();
        this.instanceId = orderData.getInfo().getInstanceId();
        this.zoneId = orderData.getZoneId();
        this.zoneCode = orderData.getZoneCode();
    }

    public String getOrderSymbol() {
        return orderSymbol;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public int getZoneId() {
        return zoneId;
    }

    public String getZoneCode() {
        return zoneCode;
    }
}
