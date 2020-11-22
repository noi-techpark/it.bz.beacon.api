package it.bz.beacon.api.exception.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No group to order")
public class NoGroupToOrderException extends RuntimeException {
}
