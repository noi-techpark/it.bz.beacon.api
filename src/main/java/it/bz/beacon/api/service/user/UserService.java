package it.bz.beacon.api.service.user;

import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.repository.UserRepository;
import it.bz.beacon.api.exception.db.DuplicateEntryException;
import it.bz.beacon.api.exception.db.AuthenticatedUserNotDeletableException;
import it.bz.beacon.api.exception.db.UserNotFoundException;
import it.bz.beacon.api.model.BaseMessage;
import it.bz.beacon.api.model.UserCreation;
import it.bz.beacon.api.model.UserUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public User create(UserCreation userCreation) {
        try {
            userCreation.setPassword(bCryptPasswordEncoder.encode(userCreation.getPassword()));
            return repository.save(User.create(userCreation));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException();
        }
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
    public BaseMessage delete(long id) throws UserNotFoundException, AuthenticatedUserNotDeletableException {
        User authUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return repository.findById(id).map(
                user -> {
                    if (user.getId().longValue() == authUser.getId().longValue()) {
                        throw new AuthenticatedUserNotDeletableException();
                    }
                    repository.delete(user);

                    return new BaseMessage("User deleted");
                }
        ).orElseThrow(UserNotFoundException::new);
    }
}
