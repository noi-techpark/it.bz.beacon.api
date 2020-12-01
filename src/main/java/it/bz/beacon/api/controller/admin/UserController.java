package it.bz.beacon.api.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/users")
public class UserController {

    @Autowired
    private IUserService service;

    @ApiOperation(value = "View a list of available users", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<User> getList() {
        return service.findAll();
    }

    @ApiOperation(value = "Search a user with an ID", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public User get(@PathVariable long id) {
        return service.find(id);
    }

    @ApiOperation(value = "Create a user", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public User create(@Valid @RequestBody UserCreation userCreation) {
        return service.create(userCreation);
    }

    @ApiOperation(value = "Update a user", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", produces = "application/json")
    public User update(@PathVariable long id, @Valid @RequestBody UserUpdate userUpdate) {
        return service.update(id, userUpdate);
    }

    @ApiOperation(value = "Delete a user", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public BaseMessage delete(@PathVariable long id) {
        return service.delete(id);
    }

    @ApiOperation(value = "Update a user", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}/change-password", produces = "application/json")
    public BaseMessage changePassword(@PathVariable long id, @Valid @RequestBody PasswordChange passwordChange) {
        return service.changePassword(id, passwordChange);
    }

    @ApiOperation(value = "Update a user", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}/reset-password", produces = "application/json")
    public BaseMessage resetPassword(@PathVariable long id, @Valid @RequestBody PasswordReset passwordReset) {
        return service.resetPassword(id, passwordReset);
    }

    @ApiOperation(value = "View a list of all the api key assigned to the groups that the user is member of", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/apiKey", produces = "application/json")
    public List<GroupApiKey> getApiKey(@PathVariable long id) {
        return service.findAllGroupApiKey(id);
    }
}
