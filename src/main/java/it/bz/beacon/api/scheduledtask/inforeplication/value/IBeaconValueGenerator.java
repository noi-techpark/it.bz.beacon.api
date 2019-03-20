package it.bz.beacon.api.scheduledtask.inforeplication.value;

import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.db.repository.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class IBeaconValueGenerator extends BaseValueGenerator<iBeaconValue> {

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private InfoRepository infoRepository;

    @Override
    protected iBeaconValue generateRandomValue() {
        return new iBeaconValue(beaconSuedtirolConfiguration.getUuid(), randomUnsignedShort(), randomUnsignedShort());
    }

    @Override
    protected boolean alreadyExists(iBeaconValue value) {
        return false;
    }

    private int randomUnsignedShort() {
        return new Random().nextInt(65536);
    }
}
