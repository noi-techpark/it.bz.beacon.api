package it.bz.beacon.api.exception.auth;

import io.jsonwebtoken.io.IOException;

public class InvalidJwtAuthenticationException extends IOException {
    private static final long serialVersionUID = 2977933495522620332L;

    public InvalidJwtAuthenticationException(String e) {
        super(e);
    }
}