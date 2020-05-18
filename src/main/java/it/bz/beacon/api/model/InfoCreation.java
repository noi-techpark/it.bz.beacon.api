package it.bz.beacon.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class InfoCreation {

    @NotEmpty
    @NotNull
    private String beaconId;

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String website;

    @NotEmpty
    @NotNull
    private String address;

    @NotEmpty
    @NotNull
    private String location;

    @NotEmpty
    @NotNull
    private String cap;

    @NotEmpty
    @NotNull
    private float latitude;

    @NotEmpty
    @NotNull
    private float longitude;

    private String floor;

    private String openDataPoiId;

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
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

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
}
