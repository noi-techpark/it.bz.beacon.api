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
