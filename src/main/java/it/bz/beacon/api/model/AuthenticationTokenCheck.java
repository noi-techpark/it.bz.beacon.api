// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

public class AuthenticationTokenCheck {
    private String token;
    private boolean valid;

    public AuthenticationTokenCheck(String token, boolean valid) {
        this.token = token;
        this.valid = valid;
    }

    public String getToken() {
        return token;
    }

    public boolean isValid() {
        return valid;
    }
}
