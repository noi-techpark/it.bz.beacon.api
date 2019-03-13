package it.bz.beacon.api.model;

import it.bz.beacon.api.kontakt.io.model.BeaconConfiguration;
import it.bz.beacon.api.kontakt.io.model.enumeration.Packet;
import it.bz.beacon.api.kontakt.io.model.enumeration.Profile;
import it.bz.beacon.api.util.EddystoneUrl;

import java.util.UUID;

public class PendingConfiguration {
    private UUID uuid;
    private Integer major;
    private Integer minor;

    private String namespace;
    private String url;
    private String instanceId;

    private Integer txPower;
    private Integer interval;

    private boolean iBeacon;
    private boolean telemetry;
    private boolean eddystoneUid;
    private boolean eddystoneUrl;
    private boolean eddystoneTlm;
    private boolean eddystoneEid;
    private boolean eddystoneEtlm;

    public static PendingConfiguration fromBeaconConfiguration(BeaconConfiguration configuration, RemoteBeacon remoteBeacon) {
        PendingConfiguration pendingConfiguration = new PendingConfiguration();

        if (configuration.getProximity() != null) {
            pendingConfiguration.setUuid(configuration.getProximity());
        } else {
            pendingConfiguration.setUuid(remoteBeacon.getUuid());
        }

        if (configuration.getMajor() != null) {
            pendingConfiguration.setMajor(configuration.getMajor());
        } else {
            pendingConfiguration.setMajor(remoteBeacon.getMajor());
        }

        if (configuration.getMinor() != null) {
            pendingConfiguration.setMinor(configuration.getMinor());
        } else {
            pendingConfiguration.setMinor(remoteBeacon.getMinor());
        }

        if (configuration.getNamespace() != null) {
            pendingConfiguration.setNamespace(configuration.getNamespace());
        } else {
            pendingConfiguration.setNamespace(remoteBeacon.getNamespace());
        }

        if (configuration.getInstanceId() != null) {
            pendingConfiguration.setInstanceId(configuration.getInstanceId());
        } else {
            pendingConfiguration.setInstanceId(remoteBeacon.getInstanceId());
        }

        if (configuration.getUrl() != null) {
            pendingConfiguration.setUrl(EddystoneUrl.decodeUri(configuration.getUrl()));
        } else {
            pendingConfiguration.setUrl(remoteBeacon.getUrl());
        }

        if (configuration.getTxPower() != null) {
            pendingConfiguration.setTxPower(configuration.getTxPower());
        } else {
            pendingConfiguration.setTxPower(remoteBeacon.getTxPower());
        }

        if (configuration.getInterval() != null) {
            pendingConfiguration.setInterval(configuration.getInterval());
        } else {
            pendingConfiguration.setInterval(remoteBeacon.getInterval());
        }

        if (configuration.getProximity() != null) {
            pendingConfiguration.setUuid(configuration.getProximity());
        } else {
            pendingConfiguration.setUuid(remoteBeacon.getUuid());
        }

        if (configuration.getProximity() != null) {
            pendingConfiguration.setUuid(configuration.getProximity());
        } else {
            pendingConfiguration.setUuid(remoteBeacon.getUuid());
        }

        if (configuration.getPackets() != null) {
            pendingConfiguration.setEddystoneEid(configuration.getPackets().contains(Packet.EDDYSTONE_EID));
            pendingConfiguration.setEddystoneEtlm(configuration.getPackets().contains(Packet.EDDYSTONE_ETLM));
            pendingConfiguration.setEddystoneTlm(configuration.getPackets().contains(Packet.EDDYSTONE_TLM));
            pendingConfiguration.setEddystoneUrl(configuration.getPackets().contains(Packet.EDDYSTONE_URL));
            pendingConfiguration.setTelemetry(configuration.getPackets().contains(Packet.KONTAKT_TLM));
            if (configuration.getProfiles() != null) {
                pendingConfiguration.setEddystoneUid(configuration.getPackets().contains(Packet.EDDYSTONE_UID) || configuration.getProfiles().contains(Profile.EDDYSTONE));
                pendingConfiguration.setiBeacon(configuration.getPackets().contains(Packet.IBEACON) || configuration.getProfiles().contains(Profile.IBEACON));
            } else {
                pendingConfiguration.setEddystoneUid(remoteBeacon.isEddystoneUid());
                pendingConfiguration.setiBeacon(remoteBeacon.isiBeacon());
            }
        } else {
            pendingConfiguration.setEddystoneEid(remoteBeacon.isEddystoneEid());
            pendingConfiguration.setEddystoneEtlm(remoteBeacon.isEddystoneEtlm());
            pendingConfiguration.setEddystoneTlm(remoteBeacon.isEddystoneTlm());
            pendingConfiguration.setEddystoneUid(remoteBeacon.isEddystoneUid());
            pendingConfiguration.setEddystoneUrl(remoteBeacon.isEddystoneUrl());
            pendingConfiguration.setiBeacon(remoteBeacon.isiBeacon());
            pendingConfiguration.setTelemetry(remoteBeacon.isTelemetry());
        }

        return pendingConfiguration;
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

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public boolean isiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(boolean iBeacon) {
        this.iBeacon = iBeacon;
    }

    public boolean isTelemetry() {
        return telemetry;
    }

    public void setTelemetry(boolean telemetry) {
        this.telemetry = telemetry;
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
