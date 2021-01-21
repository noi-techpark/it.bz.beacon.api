package it.bz.beacon.api.exception.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No new beacons to order")
public class NoBeaconsToOrderException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
