// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.util;

public class ManufacturerNameValidator {

    private final static String NAME_V1 = "^[A-Z]{4}[0-9]{3}[A-Z]#[A-Za-z0-9]{6}$";
    private final static String NAME_V2 = "^[A-Z0-9]{3}[0-9]{4}#[A-Za-z0-9]{6}$";

    public static boolean isValid(String value) {
        return value != null && (isV1(value) || isV2(value));
    }

    public static boolean isV1(String value) {
        return value.matches(NAME_V1);
    }

    public static boolean isV2(String value) {
        return value.matches(NAME_V2);
    }
}
