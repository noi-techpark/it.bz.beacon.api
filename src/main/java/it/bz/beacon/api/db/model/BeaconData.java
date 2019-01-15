package it.bz.beacon.api.db.model;

import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.model.Manufacturer;

import javax.persistence.*;

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

    public static BeaconData fromBeacon(Beacon beacon, Manufacturer manufacturer, String manufacturerId) {
        BeaconData beaconData = new BeaconData();

        beaconData.setManufacturer(manufacturer);
        beaconData.setManufacturerId(manufacturerId);

        beaconData.setName(beacon.getName());
        beaconData.setDescription(beacon.getDescription());
        beaconData.setLat(beacon.getLat());
        beaconData.setLng(beacon.getLng());

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
}
