// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import it.bz.beacon.api.model.UserCreation;
import it.bz.beacon.api.model.UserUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "\"User\"")
public class User extends AuditModel implements UserDetails {
    private static final long serialVersionUID = -1796884471421581637L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String name;

    private String surname;

    private String email;

    private boolean admin = false;

    @JsonIgnore
    private Boolean requirePasswordChange;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserRoleGroup> groups = new ArrayList<>();

    public static User create(UserCreation userCreation) {
        User user = new User();
        user.setUsername(userCreation.getUsername());
        user.setPassword(userCreation.getPassword());
        user.setName(userCreation.getName());
        user.setSurname(userCreation.getSurname());
        user.setEmail(userCreation.getEmail());
        user.setRequirePasswordChange(true);

        return user;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public List<String> getRoles() {
        if (isAdmin()) {
            return Lists.newArrayList("ADMIN");
        }
        return Lists.newArrayList();
    }

    @JsonIgnore
    public void applyUpdate(UserUpdate userUpdate) {
        name = userUpdate.getName();
        surname = userUpdate.getSurname();
        email = userUpdate.getEmail();
        admin = userUpdate.getAdmin();
    }

    public boolean isAdmin() {
        return admin;
    }

    public List<UserRoleGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserRoleGroup> groups) {
        this.groups = groups;
    }

    public Boolean getRequirePasswordChange() {
        return requirePasswordChange;
    }

    public void setRequirePasswordChange(Boolean requirePasswordChange) {
        this.requirePasswordChange = requirePasswordChange;
    }
}
