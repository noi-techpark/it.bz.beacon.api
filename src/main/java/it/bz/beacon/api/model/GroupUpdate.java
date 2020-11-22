package it.bz.beacon.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class GroupUpdate {

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String kontaktIoApiKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKontaktIoApiKey() {
        return kontaktIoApiKey;
    }

    public void setKontaktIoApiKey(String kontaktIoApiKey) {
        this.kontaktIoApiKey = kontaktIoApiKey;
    }
}
