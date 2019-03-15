package it.bz.beacon.api.scheduledtask.inforeplication.error;

import java.util.ArrayList;
import java.util.List;

public class RowError {
    private int index;
    private List<ParseError> errors = new ArrayList<>();

    public RowError(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void addError(ParseError error) {
        this.errors.add(error);
    }

    public void setErrors(List<ParseError> errors) {
        this.errors = errors;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }
}
