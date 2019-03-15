package it.bz.beacon.api.service.info;

import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InfoService implements IInfoService {

    @Autowired
    private InfoRepository repository;

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Override
    public List<Info> findAll() {
        return repository.findAll();
    }

    @Override
    public Info findByBeaconId(String beaconId) throws InfoNotFoundException {
        return repository.findById(beaconId).orElseThrow(InfoNotFoundException::new);
    }

    @Override
    public Info findByInstanceId(String instanceId) throws InfoNotFoundException {
        return repository.findByNamespaceAndInstanceId(beaconSuedtirolConfiguration.getNamespace(), instanceId).orElseThrow(InfoNotFoundException::new);
    }

    @Override
    public Info findByMajorMinor(int major, int minor) throws InfoNotFoundException {
        return repository.findByUuidAndMajorAndMinor(beaconSuedtirolConfiguration.getUuid(), major, minor).orElseThrow(InfoNotFoundException::new);
    }

//    @Override
//    public Info create(InfoCreation infoCreation) {
//        try {
//            return repository.save(Info.create(infoCreation, beaconSuedtirolConfiguration.getUuid(), beaconSuedtirolConfiguration.getNamespace()));
//        } catch (DataIntegrityViolationException e) {
//            throw new DuplicateEntryException();
//        }
//    }
//
//    @Override
//    public Info update(long id, InfoUpdate infoUpdate) throws UserNotFoundException {
//        return repository.findById(id).map(info -> {
//            if (infoUpdate.getPassword() != null) {
//
//            }
////            info.applyUpdate(infoUpdate);
////
//            return repository.save(info);
//        }).orElseThrow(UserNotFoundException::new);
//    }
}
