package it.bz.beacon.api.model;

import javax.validation.constraints.*;

public class UserCreation {

    @NotEmpty
    @NotNull
    private String username;

    @NotEmpty
    @NotNull
    @Size(min = 8, max = 32)
    @Pattern(regexp = ".*[$&+,:;=\\\\?@#|/'<>.^*()%!-].*")
    private String password;

    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String surname;

    @Email
    @NotEmpty
    @NotNull
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
