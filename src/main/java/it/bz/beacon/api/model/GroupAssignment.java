package it.bz.beacon.api.model;

import it.bz.beacon.api.model.enumeration.UserRole;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

public class GroupAssignment {

    @NotNull
    private Long user;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private UserRole userRole;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
