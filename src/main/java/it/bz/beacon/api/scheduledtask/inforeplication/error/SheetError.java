package it.bz.beacon.api.scheduledtask.inforeplication.error;

import java.util.ArrayList;
import java.util.List;

public class SheetError {
    private String title;
    private List<RowError> errors = new ArrayList<>();

    public SheetError(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addError(RowError error) {
        this.errors.add(error);
    }

    public List<RowError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }
}
