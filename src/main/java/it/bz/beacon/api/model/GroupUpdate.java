package it.bz.beacon.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class GroupUpdate {

    @NotEmpty
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
