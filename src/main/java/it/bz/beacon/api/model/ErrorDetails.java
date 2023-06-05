// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

import java.util.Date;

public class ErrorDetails {

    private Date timestamp = new Date();
    private String message;
    private String details;

    public ErrorDetails(String message) {
        this.message = message;
    }

    public ErrorDetails(String message, String details) {
        super();
        this.message = message;
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
