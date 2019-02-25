package it.bz.beacon.api.service.info;

import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.exception.db.UserNotFoundException;
import it.bz.beacon.api.model.InfoCreation;
import it.bz.beacon.api.model.InfoUpdate;

import java.util.List;

public interface IInfoService {
    List<Info> findAll();
    Info find(long id) throws UserNotFoundException;
    Info create(InfoCreation userCreation);
    Info update(long id, InfoUpdate infoUpdate) throws UserNotFoundException;
}
