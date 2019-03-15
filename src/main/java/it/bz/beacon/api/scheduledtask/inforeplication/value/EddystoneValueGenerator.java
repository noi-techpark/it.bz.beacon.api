package it.bz.beacon.api.scheduledtask.inforeplication.value;

import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.util.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EddystoneValueGenerator extends BaseValueGenerator<EddystoneValue> {

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private InfoRepository infoRepository;

    @Override
    protected EddystoneValue generateRandomValue() {
        return new EddystoneValue(beaconSuedtirolConfiguration.getNamespace(), randomInstanceId());
    }

    @Override
    protected boolean alreadyExists(EddystoneValue value) {
        try {
            infoRepository.findByNamespaceAndInstanceId(value.getNamespace(), value.getInstanceId())
                    .orElseThrow(InfoNotFoundException::new);
            return true;
        } catch (InfoNotFoundException e) {
            return false;
        }
    }

    private String randomInstanceId() {
        RandomString randomString = new RandomString(12, RandomString.HEX);
        return randomString.nextString();
    }
}
