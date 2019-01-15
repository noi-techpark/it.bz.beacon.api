package it.bz.beacon.api.model;

public class AuthenticationToken {
    private String token;

    public AuthenticationToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
