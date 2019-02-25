package it.bz.beacon.api.service.info;

import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.DuplicateEntryException;
import it.bz.beacon.api.exception.db.UserNotFoundException;

import it.bz.beacon.api.model.InfoCreation;
import it.bz.beacon.api.model.InfoUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InfoService implements IInfoService {

    @Autowired
    private InfoRepository repository;

    @Override
    public List<Info> findAll() {
        return repository.findAll();
    }

    @Override
    public Info find(long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Info create(InfoCreation infoCreation) {
        try {
            return repository.save(Info.create(infoCreation));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException();
        }
    }

    @Override
    public Info update(long id, InfoUpdate infoUpdate) throws UserNotFoundException {
        return repository.findById(id).map(info -> {
            if (infoUpdate.getPassword() != null) {

            }
//            info.applyUpdate(infoUpdate);
//
            return repository.save(info);
        }).orElseThrow(UserNotFoundException::new);
    }
}
