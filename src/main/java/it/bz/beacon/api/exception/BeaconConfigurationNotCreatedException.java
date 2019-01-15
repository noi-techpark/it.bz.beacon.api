package it.bz.beacon.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Beacon configuration not created")
public class BeaconConfigurationNotCreatedException extends RuntimeException {
}
