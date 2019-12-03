package it.bz.beacon.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.Group;
import it.bz.beacon.api.db.model.Issue;
import it.bz.beacon.api.model.enumeration.LocationType;
import it.bz.beacon.api.model.enumeration.Status;
import org.springframework.lang.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Beacon {

    private String id;
    private Manufacturer manufacturer;
    private String manufacturerId;
    private String name;
    private String description;
    private float lat;
    private float lng;
    private LocationType locationType;
    private String locationDescription;
    private long lastSeen;

    private boolean iBeacon;
    private boolean telemetry;
    private boolean eddystoneUid;
    private boolean eddystoneUrl;
    private boolean eddystoneTlm;
    private boolean eddystoneEid;
    private boolean eddystoneEtlm;

    private UUID uuid;
    private int major;
    private int minor;

    private String url;
    private String namespace;
    private String instanceId;

    private int interval;
    private int txPower;

    private Integer batteryLevel;

    private PendingConfiguration pendingConfiguration;

    private String internalName;

    private Group group;

    @JsonIgnore
    private List<Issue> issues;

    public static Beacon fromRemoteBeacon(BeaconData beaconData, RemoteBeacon remoteBeacon) {
        Beacon beacon = new Beacon();
        beacon.applyBeaconData(beaconData);
        beacon.applyRemoteBeacon(remoteBeacon);

        return beacon;
    }

    @JsonIgnore
    public void applyBeaconData(@NonNull BeaconData beaconData) {
        setId(beaconData.getId());
        setManufacturer(beaconData.getManufacturer());
        setManufacturerId(beaconData.getManufacturerId());
        setLat(beaconData.getLat());
        setLng(beaconData.getLng());
        setName(beaconData.getName());
        setDescription(beaconData.getDescription());
        setLocationType(beaconData.getLocationType());
        setLocationDescription(beaconData.getLocationDescription());
        setIssues(beaconData.getIssues());
        setGroup(beaconData.getGroup());
        if (beaconData.getBatteryLevel() != null) {
            setBatteryLevel(beaconData.getBatteryLevel());
        }
    }

    @JsonIgnore
    public void applyRemoteBeacon(RemoteBeacon remoteBeacon) {
        if (remoteBeacon != null) {
            setUuid(remoteBeacon.getUuid());
            setMajor(remoteBeacon.getMajor());
            setMinor(remoteBeacon.getMinor());
            setUrl(remoteBeacon.getUrl());
            setNamespace(remoteBeacon.getNamespace());
            setInstanceId(remoteBeacon.getInstanceId());
            setInterval(remoteBeacon.getInterval());
            setTxPower(remoteBeacon.getTxPower());
            if (getBatteryLevel() == null) {
                setBatteryLevel(remoteBeacon.getBatteryLevel());
            }

            setLastSeen(remoteBeacon.getLastSeen() * 1000);
            setiBeacon(remoteBeacon.isiBeacon());
            setTelemetry(remoteBeacon.isTelemetry());
            setEddystoneUid(remoteBeacon.isEddystoneUid());
            setEddystoneTlm(remoteBeacon.isEddystoneTlm());
            setEddystoneUrl(remoteBeacon.isEddystoneUrl());
            setEddystoneEid(remoteBeacon.isEddystoneEid());
            setEddystoneEtlm(remoteBeacon.isEddystoneEtlm());

            setPendingConfiguration(remoteBeacon.getPendingConfiguration());

            setInternalName(remoteBeacon.getName());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        }
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

    public String getUrl() {
        if (url == null) {
            return "";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNamespace() {
        if (namespace == null) {
            return "";
        }
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getInstanceId() {
        if (instanceId == null) {
            return "";
        }
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerId() {
        if (manufacturerId == null) {
            return "";
        }
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getLocationDescription() {
        if (locationDescription == null) {
            return "";
        }
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Status getStatus() {

        if (getPendingConfiguration() != null) {
            return Status.CONFIGURATION_PENDING;
        }

        Calendar checkDate = Calendar.getInstance();
        checkDate.add(Calendar.MONTH, -12);

        Date date = new Date(lastSeen);
        if (date.before(checkDate.getTime())) {
            return Status.NO_SIGNAL;
        }

        if (getIssues() != null) {
            for(Issue issue : getIssues()) {
                if (issue.getSolution() == null) {
                    return Status.ISSUE;
                }
            }
        }

        if (getBatteryLevel() != null && getBatteryLevel() < 5) {
            return Status.BATTERY_LOW;
        }

        return Status.OK;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(boolean iBeacon) {
        this.iBeacon = iBeacon;
    }

    public boolean isEddystoneUid() {
        return eddystoneUid;
    }

    public void setEddystoneUid(boolean eddystoneUid) {
        this.eddystoneUid = eddystoneUid;
    }

    public boolean isEddystoneUrl() {
        return eddystoneUrl;
    }

    public void setEddystoneUrl(boolean eddystoneUrl) {
        this.eddystoneUrl = eddystoneUrl;
    }

    public boolean isEddystoneTlm() {
        return eddystoneTlm;
    }

    public void setEddystoneTlm(boolean eddystoneTlm) {
        this.eddystoneTlm = eddystoneTlm;
    }

    public boolean isTelemetry() {
        return telemetry;
    }

    public void setTelemetry(boolean telemetry) {
        this.telemetry = telemetry;
    }

    public boolean isEddystoneEid() {
        return eddystoneEid;
    }

    public void setEddystoneEid(boolean eddystoneEid) {
        this.eddystoneEid = eddystoneEid;
    }

    public boolean isEddystoneEtlm() {
        return eddystoneEtlm;
    }

    public void setEddystoneEtlm(boolean eddystoneEtlm) {
        this.eddystoneEtlm = eddystoneEtlm;
    }

    public PendingConfiguration getPendingConfiguration() {
        return pendingConfiguration;
    }

    public void setPendingConfiguration(PendingConfiguration pendingConfiguration) {
        this.pendingConfiguration = pendingConfiguration;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return this.group;
    }

}
