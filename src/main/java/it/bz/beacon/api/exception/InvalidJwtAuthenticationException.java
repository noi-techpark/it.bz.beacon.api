package it.bz.beacon.api.exception;

import io.jsonwebtoken.io.IOException;

import javax.naming.AuthenticationException;

public class InvalidJwtAuthenticationException extends IOException {
    public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}