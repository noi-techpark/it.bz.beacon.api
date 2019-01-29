package it.bz.beacon.api.model;

import it.bz.beacon.api.kontakt.io.model.BeaconConfiguration;

import java.util.UUID;

public class PendingConfiguration {
    private UUID uuid;
    private Integer major;
    private Integer minor;
    private String namespace;
    private String url;
    private String instanceId;
    //private Integer txPower;
    //private Integer interval;

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
}
