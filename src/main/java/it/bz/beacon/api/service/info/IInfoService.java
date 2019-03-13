package it.bz.beacon.api.service.info;

import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.exception.db.InfoNotFoundException;

import java.util.List;

public interface IInfoService {
    List<Info> findAll();
    Info findByBeaconId(String beaconId) throws InfoNotFoundException;
    Info findByInstanceId(String instanceId) throws InfoNotFoundException;
    Info findByMajorMinor(int major, int minor) throws InfoNotFoundException;
//    Info create(InfoCreation userCreation);
//    Info update(long id, InfoUpdate infoUpdate) throws UserNotFoundException;
}
