package it.bz.beacon.api.model;

import it.bz.beacon.api.kontakt.io.model.TagBeaconDevice;
import it.bz.beacon.api.kontakt.io.model.enumeration.Packet;
import it.bz.beacon.api.kontakt.io.model.enumeration.Profile;
import it.bz.beacon.api.util.EddystoneUrl;

import java.util.UUID;

public class RemoteBeacon {

    private Manufacturer manufacturer;
    private String manufacturerId;
    private String name;

    private UUID uuid;
    private int major;
    private int minor;
    private String url;
    private String namespace;
    private String instanceId;
    private int interval;
    private int txPower;
    private Integer batteryLevel;
    private long lastSeen;
    private boolean iBeacon;
    private boolean telemetry;
    private boolean eddystoneUid;
    private boolean eddystoneUrl;
    private boolean eddystoneTlm;
    private boolean eddystoneEid;
    private boolean eddystoneEtlm;
    private PendingConfiguration pendingConfiguration;

    public static RemoteBeacon fromTagBeaconDevice(TagBeaconDevice tagBeaconDevice) {
        RemoteBeacon remoteBeacon = new RemoteBeacon();

        remoteBeacon.setManufacturer(Manufacturer.KONTAKT_IO);
        remoteBeacon.setManufacturerId(tagBeaconDevice.getUniqueId());
        remoteBeacon.setUuid(tagBeaconDevice.getProximity());
        remoteBeacon.setMajor(tagBeaconDevice.getMajor());
        remoteBeacon.setMinor(tagBeaconDevice.getMinor());
        remoteBeacon.setUrl(EddystoneUrl.decodeUri(tagBeaconDevice.getUrl()));
        remoteBeacon.setNamespace(tagBeaconDevice.getNamespace());
        remoteBeacon.setInstanceId(tagBeaconDevice.getInstanceId());
        remoteBeacon.setInterval(tagBeaconDevice.getInterval());
        remoteBeacon.setTxPower(tagBeaconDevice.getTxPower());
        remoteBeacon.setName(tagBeaconDevice.getName());

        remoteBeacon.setLastSeen(tagBeaconDevice.getLastSeen() * 1000);
        remoteBeacon.setiBeacon(tagBeaconDevice.getProfiles().contains(Profile.IBEACON));
        remoteBeacon.setEddystoneUid(tagBeaconDevice.getProfiles().contains(Profile.EDDYSTONE));
        remoteBeacon.setTelemetry(tagBeaconDevice.getPackets().contains(Packet.KONTAKT_TLM));
        remoteBeacon.setEddystoneTlm(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_TLM));
        remoteBeacon.setEddystoneUrl(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_URL));
        remoteBeacon.setEddystoneEid(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_EID));
        remoteBeacon.setEddystoneEtlm(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_ETLM));

        return remoteBeacon;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public PendingConfiguration getPendingConfiguration() {
        return pendingConfiguration;
    }

    public void setPendingConfiguration(PendingConfiguration pendingConfiguration) {
        this.pendingConfiguration = pendingConfiguration;
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
}
