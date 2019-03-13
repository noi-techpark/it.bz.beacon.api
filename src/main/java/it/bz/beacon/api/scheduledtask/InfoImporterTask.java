package it.bz.beacon.api.scheduledtask;

import it.bz.beacon.api.config.InfoImporterTaskConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InfoImporterTask {

    @Autowired
    private InfoImporterTaskConfiguration configuration;

    private static final Logger log = LoggerFactory.getLogger(InfoImporterTask.class);

    @Scheduled(fixedRate = 5000)
    public void startImport() {
        if (configuration.isEnabled()) {
            log.info("Starting info import...");
        }
    }
}