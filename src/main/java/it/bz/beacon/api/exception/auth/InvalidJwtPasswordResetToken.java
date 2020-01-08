package it.bz.beacon.api.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "The given password reset JWT token is not valid")
public class InvalidJwtPasswordResetToken extends RuntimeException {
}