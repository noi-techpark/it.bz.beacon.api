package it.bz.beacon.api.scheduledtask.inforeplication;

import java.util.List;

public class Zone {
    private int id;
    private String code;
    private String name;
    private String email;
    private String sheetName;

    public Zone(List<String> row) {

        this.id = Integer.parseInt(row.get(0));
        this.code = row.get(1);
        this.name = row.get(2);
        this.email = row.get(3);
        this.sheetName = row.get(4);
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
