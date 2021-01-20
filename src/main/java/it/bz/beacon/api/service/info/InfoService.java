package it.bz.beacon.api.service.info;

import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class InfoService implements IInfoService {

    final static Logger log = LoggerFactory.getLogger(InfoService.class);


    @Autowired
    private InfoRepository repository;

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Override
    public List<Info> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Info> findAllAfter(Date date) {
        return repository.findAllByUpdatedAtAfter(date);
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
}
