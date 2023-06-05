// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

public class BaseMessage {

    private String message;

    public BaseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
