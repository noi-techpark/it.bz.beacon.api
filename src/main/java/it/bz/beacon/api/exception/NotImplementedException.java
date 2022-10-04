package it.bz.beacon.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED, reason = "This action is not implemented")
public class NotImplementedException extends RuntimeException {
    private static final long serialVersionUID = -7257301845233667923L;
}