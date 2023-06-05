// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.bz.beacon.api.model.enumeration.InfoStatus;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "info")
public class Info extends AuditModel {
    private static final long serialVersionUID = -1796002278012913830L;

    @Id
    @Column(unique = true)
    private String id;

    private UUID uuid;
    private int major;
    private int minor;
    private String namespace;
    private String instanceId;

    @Type(type="org.hibernate.type.StringType")
    private String name;
    @Type(type="org.hibernate.type.StringType")
    private String website;
    @Type(type="org.hibernate.type.StringType")
    private String address;
    @Type(type="org.hibernate.type.StringType")
    private String location;
    private String cap;
    private double latitude;
    private double longitude;
    private String floor;

    private Integer batteryLevel;
    private Date trustedUpdatedAt;

    @Enumerated(EnumType.STRING)
    private InfoStatus status;

    private Integer txPower;
    private boolean online;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public InfoStatus getStatus() {
        return status;
    }

    public void setStatus(InfoStatus status) {
        this.status = status;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public void setTrustedUpdatedAt(Date trustedUpdatedAt) {
        this.trustedUpdatedAt = trustedUpdatedAt;
    }

    @ApiModelProperty(dataType = "java.lang.Long")
    public Date getTrustedUpdatedAt() {
        return trustedUpdatedAt;
    }

    @Override
    @JsonProperty
    @ApiModelProperty(dataType = "java.lang.Long")
    public Date getUpdatedAt() {
        return super.getUpdatedAt();
    }
}
