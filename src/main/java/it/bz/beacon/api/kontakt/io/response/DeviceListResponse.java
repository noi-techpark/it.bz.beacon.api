// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.bz.beacon.api.kontakt.io.model.SearchMeta;
import it.bz.beacon.api.kontakt.io.model.Device;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceListResponse<T extends Device> {

    private List<T> devices;
    private SearchMeta searchMeta;

    public List<T> getDevices() {
        return devices;
    }

    public void setDevices(List<T> devices) {
        this.devices = devices;
    }

    public SearchMeta getSearchMeta() {
        return searchMeta;
    }

    public void setSearchMeta(SearchMeta searchMeta) {
        this.searchMeta = searchMeta;
    }
}
