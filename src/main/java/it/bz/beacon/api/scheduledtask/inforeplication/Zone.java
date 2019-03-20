package it.bz.beacon.api.scheduledtask.inforeplication;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private int id;
    private String code;
    private String name;
    private String email;
    private String sheetName;

    public Zone(List<Object> row) throws InvalidZoneException {
        List<String> fields = new ArrayList<>();
        for (Object entry : row) {
            try {
                fields.add((String) entry);
            } catch (ClassCastException e) {
                throw new InvalidZoneException("Zone row contains unparseable values");
            }
        }

        this.id = Integer.parseInt(fields.get(0));
        this.code = fields.get(1);
        this.name = fields.get(2);
        this.email = fields.get(3);
        this.sheetName = fields.get(4);
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSheetName() {
        return sheetName;
    }
}
