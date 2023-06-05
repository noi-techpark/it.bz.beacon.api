// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

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
