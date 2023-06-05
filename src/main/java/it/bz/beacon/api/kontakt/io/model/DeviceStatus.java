// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStatus {
    private String uniqueId;
    private int lastEventTimestamp;
    private int avgEventInterval;
    private int batteryLevel;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getLastEventTimestamp() {
        return lastEventTimestamp;
    }

    public void setLastEventTimestamp(int lastEventTimestamp) {
        this.lastEventTimestamp = lastEventTimestamp;
    }

    public int getAvgEventInterval() {
        return avgEventInterval;
    }

    public void setAvgEventInterval(int avgEventInterval) {
        this.avgEventInterval = avgEventInterval;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
