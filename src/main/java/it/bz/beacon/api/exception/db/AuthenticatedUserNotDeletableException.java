package it.bz.beacon.api.exception.db;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Current authenticated user cannot be deleted")
public class AuthenticatedUserNotDeletableException extends RuntimeException {
}
