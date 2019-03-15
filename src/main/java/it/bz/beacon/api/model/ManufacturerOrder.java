package it.bz.beacon.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ManufacturerOrder {

    @NotNull
    @NotEmpty
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
