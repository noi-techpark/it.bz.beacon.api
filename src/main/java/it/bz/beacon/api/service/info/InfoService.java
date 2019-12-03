package it.bz.beacon.api.service.info;

import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.service.beacon.IBeaconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class InfoService implements IInfoService {


    @Autowired
    private IBeaconService beaconService;

    @Autowired
    private InfoRepository repository;

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Override
    public List<Info> findAll() {

        List<Info> infoList = repository.findAll();

        infoList.stream().forEach(info -> {
            try {
                info.setBeacon(beaconService.find(info.getId()));
            } catch (Exception e) {

            }
        });

        return infoList;
    }

    @Override
    public List<Info> findAllAfter(Date date) {
        return repository.findAllByUpdatedAtAfter(date);
    }

    @Override
    public Info findByBeaconId(String beaconId) throws InfoNotFoundException {
        Info info = repository.findById(beaconId).orElseThrow(InfoNotFoundException::new);

        try {
            info.setBeacon(beaconService.find(info.getId()));
        } catch (Exception e) {
            info.setBeacon(null);
        }

        return info;
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
