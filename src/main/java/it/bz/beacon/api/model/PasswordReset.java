package it.bz.beacon.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PasswordReset {

    @NotNull
    @NotEmpty
    @Size(min = 8, max = 32)
    @Pattern(regexp = ".*[$&+,:;=\\\\?@#|/'<>.^*()%!-].*")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
