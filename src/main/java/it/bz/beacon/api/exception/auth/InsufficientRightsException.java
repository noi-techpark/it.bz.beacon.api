package it.bz.beacon.api.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "User has not the sufficient rights to perform this action")
public class InsufficientRightsException extends RuntimeException {
    private static final long serialVersionUID = -6528603683896464056L;
}