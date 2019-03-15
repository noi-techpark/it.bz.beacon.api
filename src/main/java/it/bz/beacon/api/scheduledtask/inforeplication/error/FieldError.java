package it.bz.beacon.api.scheduledtask.inforeplication.error;

public class FieldError implements ParseError {
    private String message;
    private String fieldName;
    private String value;

    public FieldError(String message, String fieldName, String value) {
        this.message = message;
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public String getMessage() {
        return String.format("%s (Invalid value [ %s ] for field [ %s ])", message, value, fieldName);
    }
}