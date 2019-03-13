package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.model.InfoCreation;
import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "Info"
)
public class Info extends AuditModel {

    @Id
    @Column(unique = true)
    private String id;

    private String beaconId;
    private String name;
    private String website;
    private String openDataPoiId;

    private String address;
    private String location;
    private String cap;
    private float latitude;
    private float longitude;
    private String floor;

    private UUID uuid;
    private int major;
    private int minor;

    private String namespace;
    private String instanceId;

    @JsonIgnore
    private String orderSymbol;

    public static Info create(InfoCreation infoCreation, String uuid, String namespace) {
        Info info = new Info();
        info.setBeaconId(infoCreation.getBeaconId());
        info.setName(infoCreation.getName());
        info.setWebsite(infoCreation.getWebsite());
        info.setOpenDataPoiId(infoCreation.getOpenDataPoiId());
        info.setAddress(infoCreation.getAddress());
        info.setLocation(infoCreation.getLocation());
        info.setCap(infoCreation.getCap());
        info.setLatitude(infoCreation.getLatitude());
        info.setLongitude(infoCreation.getLongitude());
        info.setFloor(infoCreation.getFloor());
        info.setUuid(UUID.fromString(uuid));
        info.setNamespace(namespace);
//        info.setMajor(infoCreation.getMajor());
//        info.setMinor(infoCreation.getMinor());
//        info.setInstanceId(infoCreation.getInstanceId());
        //TODO:All Fields

        return info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpenDataPoiId() {
        return openDataPoiId;
    }

    public void setOpenDataPoiId(String openDataPoiId) {
        this.openDataPoiId = openDataPoiId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getOrderSymbol() {
        return orderSymbol;
    }

    public void setOrderSymbol(String orderSymbol) {
        this.orderSymbol = orderSymbol;
    }
}
