package it.bz.beacon.api.exception.db;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "BeaconImage not found")
public class BeaconImageNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6578373369939265013L;
}
