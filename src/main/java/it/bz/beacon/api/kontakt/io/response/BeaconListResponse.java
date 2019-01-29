package it.bz.beacon.api.kontakt.io.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.bz.beacon.api.kontakt.io.model.TagBeaconDevice;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeaconListResponse extends DeviceListResponse<TagBeaconDevice> {
}
