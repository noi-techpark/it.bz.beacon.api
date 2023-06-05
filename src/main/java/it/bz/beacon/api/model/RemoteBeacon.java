// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

import it.bz.beacon.api.exception.db.InvalidBeaconIdentifierException;
import it.bz.beacon.api.kontakt.io.model.Device.Access;
import it.bz.beacon.api.kontakt.io.model.TagBeaconDevice;
import it.bz.beacon.api.kontakt.io.model.enumeration.Packet;
import it.bz.beacon.api.kontakt.io.model.enumeration.Profile;
import it.bz.beacon.api.util.EddystoneUrl;
import it.bz.beacon.api.util.ManufacturerNameValidator;

import java.util.Objects;
import java.util.UUID;

public class RemoteBeacon {

    private Manufacturer manufacturer;
    private String manufacturerId;

    private String id;
    private String zone;
    private String zoneCode;
    private String zoneId;
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
    private Access access;

    public static RemoteBeacon fromTagBeaconDevice(TagBeaconDevice tagBeaconDevice) throws InvalidBeaconIdentifierException {
        RemoteBeacon remoteBeacon = new RemoteBeacon();

        // 2020-01-21 d@vide.bz: parseManufacturerName set internally Zone and Id too (side effect?),
        // but only if name match a specific regular expression
        // if does not match the RE, it will use the following fallback
        remoteBeacon.setId(tagBeaconDevice.getOrderId() + "_" + tagBeaconDevice.getUniqueId());
        remoteBeacon.parseManufacturerName(tagBeaconDevice.getName());

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

        remoteBeacon.setLastSeen(tagBeaconDevice.getLastSeen());
        remoteBeacon.setiBeacon(tagBeaconDevice.getProfiles().contains(Profile.IBEACON));
        remoteBeacon.setEddystoneUid(tagBeaconDevice.getProfiles().contains(Profile.EDDYSTONE));
        remoteBeacon.setTelemetry(tagBeaconDevice.getPackets().contains(Packet.KONTAKT_TLM));
        remoteBeacon.setEddystoneTlm(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_TLM));
        remoteBeacon.setEddystoneUrl(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_URL));
        remoteBeacon.setEddystoneEid(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_EID));
        remoteBeacon.setEddystoneEtlm(tagBeaconDevice.getPackets().contains(Packet.EDDYSTONE_ETLM));

        remoteBeacon.setAccess(tagBeaconDevice.getAccess());

        return remoteBeacon;
    }

    private void parseManufacturerName(String name) throws InvalidBeaconIdentifierException {
        /*
         if (!ManufacturerNameValidator.isValid(name)) {
            throw new InvalidBeaconIdentifierException();
        }
         */

        setName(name);

        if (ManufacturerNameValidator.isV1(name)) {
            String[] parts = name.split("#");
            setZone(parts[0].substring(0, 4));
            setZoneCode(parts[0].substring(4, 8));
            setId(parts[1]);
        } else if (ManufacturerNameValidator.isV2(name)) {
            String[] parts = name.split("#");
            setZoneCode(parts[0].substring(0, 3));
            setZoneId(parts[0].substring(3, 7));
            setId(parts[1]);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
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

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteBeacon)) return false;
        RemoteBeacon that = (RemoteBeacon) o;
        return getMajor() == that.getMajor() &&
                getMinor() == that.getMinor() &&
                getInterval() == that.getInterval() &&
                getTxPower() == that.getTxPower() &&
                getLastSeen() == that.getLastSeen() &&
                isiBeacon() == that.isiBeacon() &&
                isTelemetry() == that.isTelemetry() &&
                isEddystoneUid() == that.isEddystoneUid() &&
                isEddystoneUrl() == that.isEddystoneUrl() &&
                isEddystoneTlm() == that.isEddystoneTlm() &&
                isEddystoneEid() == that.isEddystoneEid() &&
                isEddystoneEtlm() == that.isEddystoneEtlm() &&
                getManufacturer() == that.getManufacturer() &&
                Objects.equals(getManufacturerId(), that.getManufacturerId()) &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getZone(), that.getZone()) &&
                Objects.equals(getZoneCode(), that.getZoneCode()) &&
                Objects.equals(getZoneId(), that.getZoneId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getUuid(), that.getUuid()) &&
                Objects.equals(getUrl(), that.getUrl()) &&
                Objects.equals(getNamespace(), that.getNamespace()) &&
                Objects.equals(getInstanceId(), that.getInstanceId()) &&
                Objects.equals(getBatteryLevel(), that.getBatteryLevel()) &&
                Objects.equals(getPendingConfiguration(), that.getPendingConfiguration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getManufacturer(), getManufacturerId(), getId(), getZone(), getZoneCode(), getZoneId(), getName(), getUuid(), getMajor(), getMinor(), getUrl(), getNamespace(), getInstanceId(), getInterval(), getTxPower(), getBatteryLevel(), getLastSeen(), isiBeacon(), isTelemetry(), isEddystoneUid(), isEddystoneUrl(), isEddystoneTlm(), isEddystoneEid(), isEddystoneEtlm(), getPendingConfiguration());
    }
}
