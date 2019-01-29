package it.bz.beacon.api.kontakt.io.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignOrderResponse {
    private long addedDevices;

    public long getAddedDevices() {
        return addedDevices;
    }

    public void setAddedDevices(long addedDevices) {
        this.addedDevices = addedDevices;
    }
}
