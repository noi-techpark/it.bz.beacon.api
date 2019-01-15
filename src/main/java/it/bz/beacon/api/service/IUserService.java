package it.bz.beacon.api.service;

import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserService {
    List<User> findAll();
    User find(long id) throws UserNotFoundException;
    User create(User user);
    User update(long id, User user) throws UserNotFoundException;
    ResponseEntity<?> delete(long id) throws UserNotFoundException;
}
