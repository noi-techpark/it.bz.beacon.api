package it.bz.beacon.api.service;

import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.repository.UserRepository;
import it.bz.beacon.api.exception.UserNotFoundException;
import it.bz.beacon.api.model.UserUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService implements IUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User find(long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User create(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public User update(long id, UserUpdate userUpdate) throws UserNotFoundException {
        return repository.findById(id).map(user -> {
            if (userUpdate.getPassword() != null) {
                userUpdate.setPassword(bCryptPasswordEncoder.encode(userUpdate.getPassword()));
            }
            user.applyUpdate(userUpdate);

            return repository.save(user);
        }).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public ResponseEntity<?> delete(long id) throws UserNotFoundException {
        return repository.findById(id).map(
                user -> {
                    repository.delete(user);

                    return ResponseEntity.ok().build();
                }
        ).orElseThrow(UserNotFoundException::new);
    }
}
