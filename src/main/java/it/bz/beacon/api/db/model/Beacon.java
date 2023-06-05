// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.db.model;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import io.swagger.annotations.ApiModelProperty;
import it.bz.beacon.api.model.Manufacturer;
import it.bz.beacon.api.model.PendingConfiguration;
import it.bz.beacon.api.model.enumeration.LocationType;
import it.bz.beacon.api.model.enumeration.Status;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "beacon")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Beacon {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Manufacturer manufacturer;

    private String manufacturerId;

    private String name;

    @Type(type = "org.hibernate.type.StringType")
    private String description;

    private float lat;

    private float lng;

    private double info_lat;

    private double info_lng;

    private LocationType locationType = LocationType.OUTDOOR;

    @Type(type = "org.hibernate.type.StringType")
    private String locationDescription;

    @ApiModelProperty(dataType = "java.lang.Long")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSeen;

    @ApiModelProperty(dataType = "java.lang.Long")
    @Temporal(TemporalType.TIMESTAMP)
    private Date trustedUpdatedAt;

    private Boolean iBeacon;
    private Boolean telemetry;
    private Boolean eddystoneUid;
    private Boolean eddystoneUrl;
    private Boolean eddystoneTlm;
    private Boolean eddystoneEid;
    private Boolean eddystoneEtlm;

    private UUID uuid;
    private Integer major;
    private Integer minor;

    private String url;
    private String namespace;
    private String instanceId;

    private Integer interval;
    private Integer txPower;

    private Integer batteryLevel;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    @Basic(fetch = FetchType.LAZY)
    private PendingConfiguration pendingConfiguration;

    private String internalName;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String namePoi;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerId() {
        if (manufacturerId == null)
            return "";
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getName() {
        if (name == null)
            return "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if (description == null)
            return "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public double getInfo_lat() {
        return info_lat;
    }

    public void setInfo_lat(double info_lat) {
        this.info_lat = info_lat;
    }

    public double getInfo_lng() {
        return info_lng;
    }

    public void setInfo_lng(double info_lng) {
        this.info_lng = info_lng;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getLocationDescription() {
        if (locationDescription == null)
            return "";
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Date getTrustedUpdatedAt() {
        return trustedUpdatedAt;
    }

    public void setTrustedUpdatedAt(Date trustedUpdatedAt) {
        this.trustedUpdatedAt = trustedUpdatedAt;
    }

    public Boolean getiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(Boolean iBeacon) {
        this.iBeacon = iBeacon;
    }

    public Boolean getTelemetry() {
        return telemetry;
    }

    public void setTelemetry(Boolean telemetry) {
        this.telemetry = telemetry;
    }

    public Boolean getEddystoneUid() {
        return eddystoneUid;
    }

    public void setEddystoneUid(Boolean eddystoneUid) {
        this.eddystoneUid = eddystoneUid;
    }

    public Boolean getEddystoneUrl() {
        return eddystoneUrl;
    }

    public void setEddystoneUrl(Boolean eddystoneUrl) {
        this.eddystoneUrl = eddystoneUrl;
    }

    public Boolean getEddystoneTlm() {
        return eddystoneTlm;
    }

    public void setEddystoneTlm(Boolean eddystoneTlm) {
        this.eddystoneTlm = eddystoneTlm;
    }

    public Boolean getEddystoneEid() {
        return eddystoneEid;
    }

    public void setEddystoneEid(Boolean eddystoneEid) {
        this.eddystoneEid = eddystoneEid;
    }

    public Boolean getEddystoneEtlm() {
        return eddystoneEtlm;
    }

    public void setEddystoneEtlm(Boolean eddystoneEtlm) {
        this.eddystoneEtlm = eddystoneEtlm;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public String getUrl() {
        if (url == null)
            return "";
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNamespace() {
        if (namespace == null)
            return "";
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getInstanceId() {
        if (instanceId == null)
            return "";
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public PendingConfiguration getPendingConfiguration() {
        return pendingConfiguration;
    }

    public void setPendingConfiguration(PendingConfiguration pendingConfiguration) {
        this.pendingConfiguration = pendingConfiguration;
    }

    public String getInternalName() {
        if (internalName == null)
            return "";
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNamePoi() {
        return namePoi;
    }

    public void setNamePoi(String namePoi) {
        this.namePoi = namePoi;
    }
}
