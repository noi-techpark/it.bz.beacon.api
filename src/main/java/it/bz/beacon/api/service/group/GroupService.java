package it.bz.beacon.api.service.group;

import it.bz.beacon.api.db.model.Group;
import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.model.UserRoleGroup;
import it.bz.beacon.api.db.repository.GroupRepository;
import it.bz.beacon.api.db.repository.UserRepository;
import it.bz.beacon.api.db.repository.UserRoleGroupRepository;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.db.*;
import it.bz.beacon.api.model.*;
import it.bz.beacon.api.model.enumeration.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupService implements IGroupService {

    @Autowired
    private GroupRepository repository;
    @Autowired
    private UserRoleGroupRepository userRoleGroupRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Group> findAll() {
        return repository.findAll();
    }

    @Override
    public Group find(long id) throws GroupNotFoundException {
        return repository.findById(id).orElseThrow(GroupNotFoundException::new);
    }

    @Override
    public Group create(GroupUpdate groupUpdate) throws DuplicateEntryException, InsufficientRightsException {
        if (isAdmin()) {
            try {
                return repository.save(Group.create(groupUpdate));
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEntryException();
            }
        }

        throw new InsufficientRightsException();
    }

    @Override
    public Group update(long id, GroupUpdate groupUpdate) throws GroupNotFoundException, InsufficientRightsException {
        return repository.findById(id).map(group -> {
            if (!isAdmin()) {
                throw new InsufficientRightsException();
            }

            group.applyUpdate(groupUpdate);

            return repository.save(group);
        }).orElseThrow(GroupNotFoundException::new);
    }

    @Override
    public BaseMessage delete(long id) throws GroupNotFoundException, AuthenticatedUserNotDeletableException,
            InsufficientRightsException {

        if (isAdmin()) {
            return repository.findById(id).map(
                    group -> {
                        repository.delete(group);

                        return new BaseMessage("Group deleted");
                    }
            ).orElseThrow(UserNotFoundException::new);
        }

        throw new InsufficientRightsException();
    }

    private boolean isAdmin() {
        try {
            return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isAdmin();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<UserRoleMapping> findAllUsers(long groupId) {
        boolean canSeeAllUsers = isAdmin();

        User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for (UserRoleGroup authUserRoleGroup : authorizedUser.getGroups()) {
            if (authUserRoleGroup.getGroup().getId() == groupId) {
                canSeeAllUsers = true;
            }
        }

        List<UserRoleMapping> ret = new ArrayList<>();
        boolean finalCanSeeAllUsers = canSeeAllUsers;
        userRoleGroupRepository.findAllUserRoleGroupByGroupId(groupId).forEach(
                userRoleGroup -> {
                    if (finalCanSeeAllUsers || userRoleGroup.getRole() == UserRole.MANAGER)
                        ret.add(UserRoleMapping.fromUserRoleGroup(userRoleGroup));
                });
        return ret;
    }

    @Override
    public UserRoleGroup assignUser(long groupId, GroupAssignment groupAssignment) throws GroupNotFoundException, InsufficientRightsException {
        return repository.findById(groupId).map(group -> {
            if (isAdmin()) {
                UserRoleGroup createUserRoleGroup = UserRoleGroup.create(groupAssignment);
                createUserRoleGroup.setGroup(group);
                createUserRoleGroup.setUser(userRepository.getOne(groupAssignment.getUser()));
                return userRoleGroupRepository.save(createUserRoleGroup);
            }
            User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for (UserRoleGroup userRoleGroup : authorizedUser.getGroups()) {
                if (userRoleGroup.getGroup().getId() == groupId && userRoleGroup.getRole() == UserRole.MANAGER) {
                    UserRoleGroup createUserRoleGroup = UserRoleGroup.create(groupAssignment);
                    createUserRoleGroup.setGroup(group);
                    createUserRoleGroup.setUser(userRepository.getOne(groupAssignment.getUser()));
                    return userRoleGroupRepository.save(createUserRoleGroup);
                }
            }

            throw new InsufficientRightsException();
        }).orElseThrow(GroupNotFoundException::new);
    }

    @Override
    public UserRoleGroup updateUserRole(long groupId, long userId, GroupUserRoleUpdate groupUserRoleUpdate) throws UserRoleGroupNotFoundException {
        return userRoleGroupRepository.findRoleByGroupAndUser(groupId, userId).map(userRoleGroup -> {
            if (isAdmin()) {
                userRoleGroupRepository.delete(userRoleGroup);
                userRoleGroup.setRole(groupUserRoleUpdate.getUserRole());
                return userRoleGroupRepository.save(userRoleGroup);
            }
            User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for (UserRoleGroup authUserRoleGroup : authorizedUser.getGroups()) {
                if (authUserRoleGroup.getGroup().getId() == groupId && authUserRoleGroup.getRole() == UserRole.MANAGER) {
                    userRoleGroup.setRole(groupUserRoleUpdate.getUserRole());
                    return userRoleGroupRepository.save(userRoleGroup);
                }
            }

            throw new InsufficientRightsException();
        }).orElseThrow(UserRoleGroupNotFoundException::new);
    }

    @Override
    public BaseMessage deleteUserAssignment(long groupId, long userId) throws UserRoleGroupNotFoundException {
        return userRoleGroupRepository.findRoleByGroupAndUser(groupId, userId).map(userRoleGroup -> {
            if (isAdmin()) {
                userRoleGroupRepository.delete(userRoleGroup);

                return new BaseMessage("Group deleted");
            }
            User authorizedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for (UserRoleGroup authUserRoleGroup : authorizedUser.getGroups()) {
                if (authUserRoleGroup.getGroup().getId() == groupId && authUserRoleGroup.getRole() == UserRole.MANAGER) {
                    userRoleGroupRepository.delete(userRoleGroup);

                    return new BaseMessage("Group deleted");
                }
            }

            throw new InsufficientRightsException();
        }).orElseThrow(UserRoleGroupNotFoundException::new);
    }
}
