package it.bz.beacon.api.model;

import it.bz.beacon.api.kontakt.io.model.BeaconConfiguration;

import java.util.UUID;

public class PendingConfiguration {
    private UUID uuid;
    private int major;
    private int minor;
    private String namespace;
    private String url;
    private String instanceId;
    //private int txPower;
    //private int interval;

    public static PendingConfiguration fromBeaconConfiguration(BeaconConfiguration configuration) {
        PendingConfiguration pendingConfiguration = new PendingConfiguration();
        pendingConfiguration.setUuid(configuration.getProximity());
        pendingConfiguration.setMajor(configuration.getMajor());
        pendingConfiguration.setMinor(configuration.getMinor());
        pendingConfiguration.setNamespace(configuration.getNamespace());
        pendingConfiguration.setInstanceId(configuration.getInstanceId());
        pendingConfiguration.setUrl(configuration.getUrl());

        return pendingConfiguration;
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
}
