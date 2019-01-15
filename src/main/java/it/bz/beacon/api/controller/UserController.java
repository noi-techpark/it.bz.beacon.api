package it.bz.beacon.api.controller;

import io.swagger.annotations.ApiOperation;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.model.UserUpdate;
import it.bz.beacon.api.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/users")
public class UserController {

    @Autowired
    private IUserService service;

    @ApiOperation(value = "View a list of available users")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<User> getList() {
        return service.findAll();
    }

    @ApiOperation(value = "Search a user with an ID")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public User get(@PathVariable long id) {
        return service.find(id);
    }

    @ApiOperation(value = "Create a user")
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public User create(@RequestBody User user) {
        return service.create(user);
    }

    @ApiOperation(value = "Update a user")
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", produces = "application/json")
    public User update(@PathVariable long id, @RequestBody UserUpdate userUpdate) {
        return service.update(id, userUpdate);
    }

    @ApiOperation(value = "Delete a user")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable long id) {
        return service.delete(id);
    }
}
