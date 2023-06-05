// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.model;

import com.google.common.collect.Sets;

import java.util.Set;

public class DeviceUpdate {

    private String uniqueId;
    private String alias;
    private Set<String> tags = Sets.newHashSet();
    private Object metadata;
    private float lat;
    private float lng;
    private float deployedLat;
    private float deployedLng;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getDeployedLat() {
        return deployedLat;
    }

    public void setDeployedLat(float deployedLat) {
        this.deployedLat = deployedLat;
    }

    public float getDeployedLng() {
        return deployedLng;
    }

    public void setDeployedLng(float deployedLng) {
        this.deployedLng = deployedLng;
    }
}
