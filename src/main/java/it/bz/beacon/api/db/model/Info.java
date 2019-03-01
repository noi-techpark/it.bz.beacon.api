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

    private UUID uuid = UUID.fromString("6a84c716-0f2a-1ce9-f210-6a63bd873dd9");
    private int major;
    private int minor;

    private String namespace = "6a84c7166a63bd873dd9";
    private String instanceId;

    @JsonIgnore
    private String orderSymbol;

    public static Info create(InfoCreation infoCreation) {
        Info info = new Info();
        info.setBeaconId(infoCreation.getBeaconId());
        info.setName(infoCreation.getNameDe());
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
}
