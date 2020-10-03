package it.bz.beacon.api.scheduledtask.inforeplication;

import it.bz.beacon.api.config.InfoImporterTaskConfiguration;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.repository.BeaconDataRepository;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.service.beacon.BeaconService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Service
public class InfoReplicationTask {

    @Autowired
    private InfoImporterTaskConfiguration importerConfiguration;

    @Autowired
    private BeaconDataRepository beaconDataRepository;

    @Autowired
    private BeaconService beaconService;

    private static final Logger log = LoggerFactory.getLogger(InfoReplicationTask.class);

    @Scheduled(initialDelay = 5 * 1000, fixedDelayString = "${it.bz.beacon.task.infoimport.delay:10800000}")
    public void startImport() {
        if (importerConfiguration.isEnabled()) {
            try {
                updateBeaconPackageData();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("An unexcpected error occurred: " + e.getMessage());
            }
        }
    }

    private void updateBeaconPackageData() {
        List<Beacon> beacons = beaconService.findAll();

        for (Beacon beacon : beacons) {
            try {
                BeaconData beaconData = beaconDataRepository.findById(beacon.getId()).orElseThrow(BeaconNotFoundException::new);
                beaconData.setUuid(beacon.getUuid());
                beaconData.setMajor(beacon.getMajor());
                beaconData.setMinor(beacon.getMinor());
                beaconData.setNamespace(beacon.getNamespace());
                beaconData.setInstanceId(beacon.getInstanceId());

                beaconDataRepository.save(beaconData);
            } catch (InfoNotFoundException e) {
                e.printStackTrace();
                //TODO handle
            }
        }
    }

}