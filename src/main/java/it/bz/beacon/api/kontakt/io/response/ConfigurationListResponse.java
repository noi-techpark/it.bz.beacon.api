// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.bz.beacon.api.kontakt.io.model.BeaconConfiguration;
import it.bz.beacon.api.kontakt.io.model.SearchMeta;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationListResponse {

    private List<BeaconConfiguration> configs;
    private SearchMeta searchMeta;

    public List<BeaconConfiguration> getConfigs() {
        return configs;
    }

    public void setConfigs(List<BeaconConfiguration> configs) {
        this.configs = configs;
    }

    public SearchMeta getSearchMeta() {
        return searchMeta;
    }

    public void setSearchMeta(SearchMeta searchMeta) {
        this.searchMeta = searchMeta;
    }
}