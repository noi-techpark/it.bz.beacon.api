package it.bz.beacon.api.model;

import it.bz.beacon.api.model.enumeration.LocationType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

public class BeaconUpdate {

    private String name;
    private String description;
    private Float lat;
    private Float lng;
    private LocationType locationType;
    private String locationDescription;

    private Boolean iBeacon;
    private Boolean telemetry;
    private Boolean eddystoneUid;
    private Boolean eddystoneUrl;
    private Boolean eddystoneTlm;
    private Boolean eddystoneEid;
    private Boolean eddystoneEtlm;

    private UUID uuid;
    @Min(0)
    @Max(65535)
    private Integer major;
    @Min(0)
    @Max(65535)
    private Integer minor;

    private String url;
    private String namespace;
    private String instanceId;

    @Min(100)
    @Max(10240)
    private Integer interval;
    @Min(1)
    @Max(7)
    private Integer txPower;

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

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
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

    public Boolean isiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(boolean iBeacon) {
        this.iBeacon = iBeacon;
    }

    public Boolean isEddystoneUid() {
        return eddystoneUid;
    }

    public void setEddystoneUid(boolean eddystoneUid) {
        this.eddystoneUid = eddystoneUid;
    }

    public Boolean isEddystoneUrl() {
        return eddystoneUrl;
    }

    public void setEddystoneUrl(boolean eddystoneUrl) {
        this.eddystoneUrl = eddystoneUrl;
    }

    public Boolean isEddystoneTlm() {
        return eddystoneTlm;
    }

    public void setEddystoneTlm(boolean eddystoneTlm) {
        this.eddystoneTlm = eddystoneTlm;
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
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Boolean isTelemetry() {
        return telemetry;
    }

    public void setTelemetry(boolean telemetry) {
        this.telemetry = telemetry;
    }

    public Boolean isEddystoneEid() {
        return eddystoneEid;
    }

    public void setEddystoneEid(boolean eddystoneEid) {
        this.eddystoneEid = eddystoneEid;
    }

    public Boolean isEddystoneEtlm() {
        return eddystoneEtlm;
    }

    public void setEddystoneEtlm(boolean eddystoneEtlm) {
        this.eddystoneEtlm = eddystoneEtlm;
    }
}
