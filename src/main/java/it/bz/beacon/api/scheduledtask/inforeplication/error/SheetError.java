package it.bz.beacon.api.scheduledtask.inforeplication.error;

import it.bz.beacon.api.scheduledtask.inforeplication.Zone;

import java.util.ArrayList;
import java.util.List;

public class SheetError {
    private Zone zone;
    private List<RowError> errors = new ArrayList<>();

    public SheetError(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }

    public String getTitle() {
        return zone.getSheetName();
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
