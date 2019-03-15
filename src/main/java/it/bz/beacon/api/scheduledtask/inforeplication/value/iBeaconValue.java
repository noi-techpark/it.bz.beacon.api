package it.bz.beacon.api.scheduledtask.inforeplication.value;

import java.util.UUID;

public class iBeaconValue {
    private UUID uuid;
    private int major;
    private int minor;

    public iBeaconValue(UUID uuid, int major, int minor) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }
}
