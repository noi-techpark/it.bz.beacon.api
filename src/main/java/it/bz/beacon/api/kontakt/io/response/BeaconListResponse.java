// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.bz.beacon.api.kontakt.io.model.TagBeaconDevice;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeaconListResponse extends DeviceListResponse<TagBeaconDevice> {
}
