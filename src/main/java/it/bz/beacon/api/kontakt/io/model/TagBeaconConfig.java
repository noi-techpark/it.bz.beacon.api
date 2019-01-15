package it.bz.beacon.api.kontakt.io.model;

import it.bz.beacon.api.kontakt.io.model.enumeration.ButtonFeature;
import it.bz.beacon.api.kontakt.io.model.enumeration.Packet;
import it.bz.beacon.api.kontakt.io.model.enumeration.Model;
import it.bz.beacon.api.kontakt.io.model.enumeration.Profile;

import java.util.Set;
import java.util.UUID;

public class TagBeaconConfig {
    private String uniqueId;
    private String name;
    private Model model;
    private Set<Profile> profiles;
    private Set<Packet> packets;
    private UUID proximity;
    private Integer major;
    private Integer minor;
    private String namespace;
    private String instanceId;
    private String url;
    private Boolean shuffled;
    private Integer interval;
    private Set<Integer> rss1m;
    private Set<Integer> rss0m;
    private String password;
    private String customConfiguration;
    private Integer txPower;
    private Set<ButtonFeature> buttonFeatures;
    private Integer panicDuration;
    private String eidIdentityKey;
    private Integer eidRotationPeriodExponent;
    private Accelerometer accelerometer;
    private PowerSaving powerSaving;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public Set<Packet> getPackets() {
        return packets;
    }

    public void setPackets(Set<Packet> packets) {
        this.packets = packets;
    }

    public UUID getProximity() {
        return proximity;
    }

    public void setProximity(UUID proximity) {
        this.proximity = proximity;
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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getShuffled() {
        return shuffled;
    }

    public void setShuffled(Boolean shuffled) {
        this.shuffled = shuffled;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Set<Integer> getRss1m() {
        return rss1m;
    }

    public void setRss1m(Set<Integer> rss1m) {
        this.rss1m = rss1m;
    }

    public Set<Integer> getRss0m() {
        return rss0m;
    }

    public void setRss0m(Set<Integer> rss0m) {
        this.rss0m = rss0m;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCustomConfiguration() {
        return customConfiguration;
    }

    public void setCustomConfiguration(String customConfiguration) {
        this.customConfiguration = customConfiguration;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public Set<ButtonFeature> getButtonFeatures() {
        return buttonFeatures;
    }

    public void setButtonFeatures(Set<ButtonFeature> buttonFeatures) {
        this.buttonFeatures = buttonFeatures;
    }

    public Integer getPanicDuration() {
        return panicDuration;
    }

    public void setPanicDuration(Integer panicDuration) {
        this.panicDuration = panicDuration;
    }

    public String getEidIdentityKey() {
        return eidIdentityKey;
    }

    public void setEidIdentityKey(String eidIdentityKey) {
        this.eidIdentityKey = eidIdentityKey;
    }

    public Integer getEidRotationPeriodExponent() {
        return eidRotationPeriodExponent;
    }

    public void setEidRotationPeriodExponent(Integer eidRotationPeriodExponent) {
        this.eidRotationPeriodExponent = eidRotationPeriodExponent;
    }

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    public PowerSaving getPowerSaving() {
        return powerSaving;
    }

    public void setPowerSaving(PowerSaving powerSaving) {
        this.powerSaving = powerSaving;
    }
}
