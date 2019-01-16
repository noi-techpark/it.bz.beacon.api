package it.bz.beacon.api.service;

import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.exception.UserNotFoundException;
import it.bz.beacon.api.model.BaseMessage;
import it.bz.beacon.api.model.UserCreation;
import it.bz.beacon.api.model.UserUpdate;

import java.util.List;

public interface IUserService {
    List<User> findAll();
    User find(long id) throws UserNotFoundException;
    User create(UserCreation userCreation);
    User update(long id, UserUpdate userUpdate) throws UserNotFoundException;
    BaseMessage delete(long id) throws UserNotFoundException;
}
