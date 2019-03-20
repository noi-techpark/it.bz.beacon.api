package it.bz.beacon.api.scheduledtask.inforeplication.value;

import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.util.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeaconIdGenerator extends BaseValueGenerator<String> {

    @Autowired
    private InfoRepository infoRepository;

    @Override
    protected String generateRandomValue() {
        RandomString randomString = new RandomString(6, RandomString.ALPHANUMERIC);
        return randomString.nextString();
    }

    @Override
    protected boolean alreadyExists(String value) {
        try {
            infoRepository.findById(value).orElseThrow(InfoNotFoundException::new);
            return true;
        } catch (InfoNotFoundException e) {
            return false;
        }
    }
}
