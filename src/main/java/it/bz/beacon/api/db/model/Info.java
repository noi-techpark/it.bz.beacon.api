package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import it.bz.beacon.api.model.RemoteBeacon;
import it.bz.beacon.api.model.enumeration.InfoStatus;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

@Entity
@Table( name = "Info" )
public class Info extends AuditModel {

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

    @JsonIgnore
    @OneToMany(mappedBy = "beaconData", fetch = FetchType.LAZY)
    private List<Issue> issues = new ArrayList<>();

    @Formula("(select beacon_data.battery_level from beacon_data where beacon_data.id = ID)")
    @JsonIgnore
    private Integer batteryLevel;

    @Formula("(select beacon_data.trusted_updated_at from beacon_data where beacon_data.id = ID)")
    private Date trustedUpdatedAt;

    @JsonIgnore
    @Transient
    private RemoteBeacon remoteBeacon;

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

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public RemoteBeacon getRemoteBeacon() {
        return remoteBeacon;
    }

    public void setRemoteBeacon(RemoteBeacon remoteBeacon) {
        this.remoteBeacon = remoteBeacon;
    }

    public boolean isOnline() {
        Calendar checkDate = Calendar.getInstance();
        checkDate.add(Calendar.MONTH, -12);

        return !(issues == null || issues.isEmpty())
                && trustedUpdatedAt != null && trustedUpdatedAt.after(checkDate.getTime())
                && batteryLevel != null && batteryLevel >= 5;
    }

    public InfoStatus getStatus() {

        if (remoteBeacon == null)
            return null;

        if (remoteBeacon.getPendingConfiguration() != null) {
            return InfoStatus.PLANED;
        }

        return InfoStatus.INSTALLED;
    }

    public Integer getTxPower() {
        return remoteBeacon != null ? remoteBeacon.getTxPower() : null;
    }

    public void setTrustedUpdatedAt(Date trustedUpdatedAt) {
        this.trustedUpdatedAt = trustedUpdatedAt;
    }

    @ApiModelProperty(dataType = "java.lang.Long")
    public Date getTrustedUpdatedAt() {
        return trustedUpdatedAt;
    }
}
