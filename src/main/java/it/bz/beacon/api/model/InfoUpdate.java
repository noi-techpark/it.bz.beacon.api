// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class InfoUpdate {

    @Nullable
    private String name;

    @Nullable
    private String address;

    @Nullable
    private String location;

    @Nullable
    private String cap;

    @NotNull
    @Min(-90)
    @Max(90)
    private float latitude;

    @NotNull
    @Min(-180)
    @Max(180)
    private float longitude;

    @Nullable
    private String floor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getLocation() {
        return location;
    }

    public void setLocation(@Nullable String location) {
        this.location = location;
    }

    @Nullable
    public String getCap() {
        return cap;
    }

    public void setCap(@Nullable String cap) {
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

    @Nullable
    public String getFloor() {
        return floor;
    }

    public void setFloor(@Nullable String floor) {
        this.floor = floor;
    }
}