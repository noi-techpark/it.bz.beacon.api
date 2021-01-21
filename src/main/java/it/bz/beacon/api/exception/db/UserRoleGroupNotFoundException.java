package it.bz.beacon.api.exception.db;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "UserRoleGroup not found")
public class UserRoleGroupNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
}
