package it.bz.beacon.api.service.user;

import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.auth.InvalidPasswordException;
import it.bz.beacon.api.exception.db.AuthenticatedUserNotDeletableException;
import it.bz.beacon.api.exception.db.UserNotFoundException;
import it.bz.beacon.api.model.*;

import java.util.List;

public interface IUserService {
    List<User> findAll();
    User find(long id) throws UserNotFoundException;
    User create(UserCreation userCreation);
    User update(long id, UserUpdate userUpdate) throws UserNotFoundException;
    BaseMessage delete(long id) throws UserNotFoundException, AuthenticatedUserNotDeletableException;
    BaseMessage resetPassword(long id, PasswordReset passwordReset) throws UserNotFoundException,
            InsufficientRightsException;
    BaseMessage changePassword(long id, PasswordChange passwordChange) throws InvalidPasswordException,
            UserNotFoundException;

    List<GroupApiKey> findAllGroupApiKey(long id);
}
