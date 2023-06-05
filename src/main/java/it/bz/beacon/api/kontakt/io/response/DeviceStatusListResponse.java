// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.bz.beacon.api.kontakt.io.model.DeviceStatus;
import it.bz.beacon.api.kontakt.io.model.SearchMeta;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStatusListResponse {

    private List<DeviceStatus> statuses;
    private SearchMeta searchMeta;

    public List<DeviceStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<DeviceStatus> statuses) {
        this.statuses = statuses;
    }

    public SearchMeta getSearchMeta() {
        return searchMeta;
    }

    public void setSearchMeta(SearchMeta searchMeta) {
        this.searchMeta = searchMeta;
    }
}