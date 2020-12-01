package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import it.bz.beacon.api.model.Manufacturer;
import it.bz.beacon.api.model.RemoteBeacon;
import it.bz.beacon.api.model.enumeration.LocationType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"manufacturerId", "manufacturer"})
})
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class BeaconData extends AuditModel {
    private static final long serialVersionUID = -8817958472953525892L;

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date userUpdatedAt;

    @Column(nullable = false)
    private String manufacturerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Manufacturer manufacturer;

    private float latBeacon;

    private float lngBeacon;

    private String name;

    @Type(type="org.hibernate.type.StringType")
    private String description;

    @Column(nullable = false)
    private LocationType locationType = LocationType.OUTDOOR;

    @Type(type="org.hibernate.type.StringType")
    private String locationDescription;

    private Integer batteryLevel;

    @OneToMany(mappedBy = "beaconData", fetch = FetchType.LAZY)
    private List<Issue> issues = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false, updatable = true)
    private Group group;

    @Column(name = "trusted_updated_at")
    private Date trustedUpdatedAt;

    @Type(type = "org.hibernate.type.StringType")
    private String namePoi;
    @Type(type = "org.hibernate.type.StringType")
    private String address;
    @Type(type = "org.hibernate.type.StringType")
    private String location;
    private String cap;
    private double latPoi;
    private double lngPoi;
    private String floor;

    private UUID uuid;
    private int major;
    private int minor;
    private String namespace;
    private String instanceId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private RemoteBeacon remoteBeacon;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date remoteBeaconUpdatedAt;

    public static BeaconData fromRemoteBeacon(RemoteBeacon remoteBeacon) {

        BeaconData beaconData = new BeaconData();

        beaconData.setId(remoteBeacon.getId());

        beaconData.setManufacturer(remoteBeacon.getManufacturer());
        beaconData.setManufacturerId(remoteBeacon.getManufacturerId());

        //TODO set name from info if possible
        beaconData.setName(remoteBeacon.getName());

        return beaconData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUserUpdatedAt() {
        return userUpdatedAt;
    }

    public void setUserUpdatedAt(Date userUpdatedAt) {
        this.userUpdatedAt = userUpdatedAt;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public float getLatBeacon() {
        return latBeacon;
    }

    public void setLatBeacon(float latBeacon) {
        this.latBeacon = latBeacon;
    }

    public float getLngBeacon() {
        return lngBeacon;
    }

    public void setLngBeacon(float lng) {
        this.lngBeacon = lng;
    }

    public double getLatPoi() {
        return latPoi;
    }

    public void setLatPoi(double info_lat) {
        this.latPoi = info_lat;
    }

    public double getLngPoi() {
        return lngPoi;
    }

    public void setLngPoi(double info_lng) {
        this.lngPoi = info_lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Date getTrustedUpdatedAt() {
        return trustedUpdatedAt;
    }

    public void setTrustedUpdatedAt(Date trustedUpdatedAt) {
        this.trustedUpdatedAt = trustedUpdatedAt;
    }

    public String getNamePoi() {
        return namePoi;
    }

    public void setNamePoi(String namePoi) {
        this.namePoi = namePoi;
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

    public RemoteBeacon getRemoteBeacon() {
        return remoteBeacon;
    }

    public void setRemoteBeacon(RemoteBeacon remoteBeacon) {
        this.remoteBeacon = remoteBeacon;
    }

    public Date getRemoteBeaconUpdatedAt() {
        return remoteBeaconUpdatedAt;
    }

    public void setRemoteBeaconUpdatedAt(Date remoteBeaconUpdatedAt) {
        this.remoteBeaconUpdatedAt = remoteBeaconUpdatedAt;
    }
}
