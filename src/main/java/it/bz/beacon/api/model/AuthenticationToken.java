// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

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
