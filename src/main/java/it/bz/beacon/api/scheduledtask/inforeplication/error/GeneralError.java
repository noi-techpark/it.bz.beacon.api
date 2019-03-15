package it.bz.beacon.api.scheduledtask.inforeplication.error;

public class GeneralError implements ParseError {
    private String message;

    public GeneralError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}