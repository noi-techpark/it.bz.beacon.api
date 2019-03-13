package it.bz.beacon.api.db.model;

import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.model.Manufacturer;
import it.bz.beacon.api.model.RemoteBeacon;
import it.bz.beacon.api.model.enumeration.LocationType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"manufacturerId", "manufacturer"})
})
public class BeaconData extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String manufacturerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Manufacturer manufacturer;

    private float lat;

    private float lng;

    private String name;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocationType locationType = LocationType.OUTDOOR;

    @Lob
    private String locationDescription;

    @OneToMany(mappedBy = "beaconData", fetch = FetchType.EAGER)
    private List<Issue> issues = new ArrayList<>();

    public static BeaconData fromRemoteBeacon(RemoteBeacon remoteBeacon) {

        BeaconData beaconData = new BeaconData();

        beaconData.setManufacturer(remoteBeacon.getManufacturer());
        beaconData.setManufacturerId(remoteBeacon.getManufacturerId());
        if (remoteBeacon.getName() != null && !remoteBeacon.getName().isEmpty() && !remoteBeacon.getName().equals("Kontakt")) {
            beaconData.setName(remoteBeacon.getName());
        }

        return beaconData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
