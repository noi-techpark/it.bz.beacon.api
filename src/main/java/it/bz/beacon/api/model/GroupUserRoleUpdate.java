package it.bz.beacon.api.model;

import it.bz.beacon.api.model.enumeration.UserRole;

import javax.validation.constraints.NotNull;

public class GroupUserRoleUpdate {

    @NotNull
    private UserRole userRole;

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
