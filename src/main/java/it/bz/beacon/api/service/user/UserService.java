package it.bz.beacon.api.service.user;

import it.bz.beacon.api.db.model.User;
import it.bz.beacon.api.db.repository.UserRepository;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.auth.InvalidPasswordException;
import it.bz.beacon.api.exception.db.AuthenticatedUserNotDeletableException;
import it.bz.beacon.api.exception.db.DuplicateEntryException;
import it.bz.beacon.api.exception.db.UserNotFoundException;
import it.bz.beacon.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService implements IUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User find(long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User create(UserCreation userCreation) throws DuplicateEntryException, InsufficientRightsException {
        if (isAdmin()) {
            try {
                userCreation.setPassword(passwordEncoder.encode(userCreation.getPassword()));
                return repository.save(User.create(userCreation));
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateEntryException();
            }
        }

        throw new InsufficientRightsException();
    }

    @Override
    public User update(long id, UserUpdate userUpdate) throws UserNotFoundException {
        User authUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return repository.findById(id).map(user -> {
            if (!isAdmin() && !authUser.getUsername().equals(user.getUsername())) {
                throw new InsufficientRightsException();
            }

            user.applyUpdate(userUpdate);

            return repository.save(user);
        }).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public BaseMessage delete(long id) throws UserNotFoundException, AuthenticatedUserNotDeletableException,
            InsufficientRightsException {

        if (isAdmin()) {
            User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

        throw new InsufficientRightsException();
    }

    @Override
    public BaseMessage resetPassword(long id, PasswordReset passwordReset) throws UserNotFoundException,
            InsufficientRightsException {
        if (isAdmin()) {
            return repository.findById(id).map(
                    user -> {
                        user.setPassword(passwordEncoder.encode(passwordReset.getNewPassword()));
                        repository.save(user);

                        return new BaseMessage("Password reset");
                    }
            ).orElseThrow(UserNotFoundException::new);
        }

        throw new InsufficientRightsException();
    }

    @Override
    public BaseMessage changePassword(long id, PasswordChange passwordChange) throws InvalidPasswordException,
            UserNotFoundException {
        return repository.findById(id).map(
                user -> {
                    if (!passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword())) {
                        throw new InvalidPasswordException();
                    }

                    user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
                    repository.save(user);
                    return new BaseMessage("Password changed");
                }
        ).orElseThrow(UserNotFoundException::new);
    }

    private boolean isAdmin() {
        try {
            return ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isAdmin();
        } catch (Exception e) {
            return false;
        }
    }
}
