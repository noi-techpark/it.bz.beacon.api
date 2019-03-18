package it.bz.beacon.api.util;

public class ManufacturerNameValidator {
    public static boolean isValid(String value) {
        return value != null && value.matches("^[A-Z]{4}[0-9]{3}[A-Z]#[A-Za-z0-9]{6}$");
    }
}
