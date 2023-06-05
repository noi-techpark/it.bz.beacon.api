// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.Group;
import it.bz.beacon.api.db.model.UserRoleGroup;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.service.group.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/groups")
public class GroupController {

    @Autowired
    private IGroupService service;

    @ApiOperation(value = "View a list of available groups", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Group> getList() {
        return service.findAll();
    }

    @ApiOperation(value = "Search a group with an ID", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Group get(@PathVariable long id) {
        return service.find(id);
    }

    @ApiOperation(value = "Create a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public Group create(@Valid @RequestBody GroupUpdate groupUpdate) {
        return service.create(groupUpdate);
    }

    @ApiOperation(value = "Update a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", produces = "application/json")
    public Group update(@PathVariable long id, @Valid @RequestBody GroupUpdate groupUpdate) {
        return service.update(id, groupUpdate);
    }

    @ApiOperation(value = "Delete a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public BaseMessage delete(@PathVariable long id) {
        return service.delete(id);
    }

    @ApiOperation(value = "View a list of all assigned user of a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{groupId}/users", produces = "application/json")
    public List<UserRoleMapping> getUserList(@PathVariable long groupId) {
        return service.findAllUsers(groupId);
    }

    @ApiOperation(value = "Assign a user to a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{groupId}/users", produces = "application/json")
    public UserRoleGroup createUser(@PathVariable long groupId, @Valid @RequestBody GroupAssignment groupAssignment) {
        return service.assignUser(groupId, groupAssignment);
    }

    @ApiOperation(value = "Update the role of a user in a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{groupId}/users/{userId}", produces = "application/json")
    public UserRoleGroup updateUser(@PathVariable long groupId,
                                    @PathVariable long userId,
                                    @Valid @RequestBody GroupUserRoleUpdate groupUserRoleUpdate) {
        return service.updateUserRole(groupId, userId, groupUserRoleUpdate);
    }

    @ApiOperation(value = "Unassign a user from a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{groupId}/users/{userId}", produces = "application/json")
    public BaseMessage deleteUser(@PathVariable long groupId,
                                  @PathVariable long userId) {
        return service.deleteUserAssignment(groupId, userId);
    }

    @ApiOperation(value = "View the assigned api key of a group", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/apiKey", produces = "application/json")
    public GroupApiKey getApiKey(@PathVariable long id) {
        return service.findGroupApiKey(id);
    }
}
