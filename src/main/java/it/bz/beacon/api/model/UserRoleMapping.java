// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.model.UserRoleGroup;
import it.bz.beacon.api.model.enumeration.UserRole;
import org.springframework.lang.NonNull;

public class UserRoleMapping {

    private User user;
    private UserRole role;

    public static UserRoleMapping fromUserRoleGroup(@NonNull UserRoleGroup userRoleGroup) {
        UserRoleMapping ret = new UserRoleMapping();
        ret.applyUserRoleGroup(userRoleGroup);
        return ret;
    }

    @JsonIgnore
    private void applyUserRoleGroup(@NonNull UserRoleGroup userRoleGroup) {
        setUser(userRoleGroup.getUser());
        setRole(userRoleGroup.getRole());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
