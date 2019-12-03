package it.bz.beacon.api.service.group;

import it.bz.beacon.api.db.model.Group;
import it.bz.beacon.api.db.model.UserRoleGroup;
import it.bz.beacon.api.exception.db.GroupNotFoundException;
import it.bz.beacon.api.exception.db.UserRoleGroupNotFoundException;
import it.bz.beacon.api.model.*;

import java.util.List;

public interface IGroupService {
    List<Group> findAll();

    Group find(long id) throws GroupNotFoundException;

    Group create(GroupUpdate groupUpdate);

    Group update(long id, GroupUpdate groupUpdate) throws GroupNotFoundException;

    BaseMessage delete(long id) throws GroupNotFoundException;

    List<UserRoleMapping> findAllUsers(long groupId);

    UserRoleGroup assignUser(long groupId, GroupAssignment groupAssignment) throws GroupNotFoundException;

    UserRoleGroup updateUserRole(long groupId, long userId, GroupUserRoleUpdate groupUserRoleUpdate) throws UserRoleGroupNotFoundException;

    BaseMessage deleteUserAssignment(long groupId, long userId) throws UserRoleGroupNotFoundException;
}
