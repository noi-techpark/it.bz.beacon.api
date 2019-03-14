package it.bz.beacon.api.scheduledtask.inforeplication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.config.InfoImporterTaskConfiguration;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.service.beacon.BeaconService;
import it.bz.beacon.api.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Component
public class InfoReplicationTask {

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private InfoImporterTaskConfiguration importerConfiguration;

    @Autowired
    private InfoRepository repository;

    @Autowired
    private BeaconService beaconService;

    private static final Logger log = LoggerFactory.getLogger(InfoReplicationTask.class);

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void startImport() {
        if (importerConfiguration.isEnabled()) {
            replicateGoogleSheet();
            updateBeaconPackageData();
        }
    }

    private void updateBeaconPackageData() {
        List<Beacon> beacons = beaconService.findAll();
        for (Beacon beacon : beacons) {
            try {
                Info info = repository.findById(beacon.getId()).orElseThrow(InfoNotFoundException::new);
                info.setUuid(beacon.getUuid());
                info.setMajor(beacon.getMajor());
                info.setMinor(beacon.getMinor());
                info.setNamespace(beacon.getNamespace());
                info.setInstanceId(beacon.getInstanceId());

                repository.save(info);
            } catch (InfoNotFoundException e) {
                //TODO handle
            }
        }
    }

    private void replicateGoogleSheet() {
        try {
            log.info("Starting info import...");
            Sheets sheetService = getSheetsService();
            Spreadsheet spreadsheet = sheetService.spreadsheets().get(importerConfiguration.getSpreadSheetId()).execute();
            for (Sheet sheet : spreadsheet.getSheets()) {
                if (sheet.getProperties().getTitle().equals("Zones")) {
                    //TODO handle zones sheet
                    continue;
                }

                String title = sheet.getProperties().getTitle();

                log.info(String.format("Reading sheet [ %s ]", title));

                BatchGetValuesResponse response = sheetService.spreadsheets().values()
                        .batchGet(importerConfiguration.getSpreadSheetId()).setRanges(Collections.singletonList(title))
                        .execute();

                for (ValueRange valueRange : response.getValueRanges()) {
                    List<List<Object>> rows = valueRange.getValues();
                    for (int i = 1; i < rows.size(); i++) {
                        List<Object> row = rows.get(i);

                        String beaconId = (String) row.get(0);

                        try {
                            Info info;
                            if (beaconId != null) {
                                info = updateInfo(beaconId, row);
                                log.info(String.format("Info updated [ %s ]", info.getId()));
                            } else {
                                info = createInfo(sheetService, row);
                                log.info(String.format("Info created [ %s ]", info.getId()));
                            }
                        } catch (Exception e) {
                            log.info(String.format("Row data corrupt #%s", i));
                            //TODO inform about data inconsistency
                        }
                    }
                }

                log.info("Tschuff Tschuff Tschuff, die Eisenbahn...");
            }
        } catch (IOException | GeneralSecurityException e) {
            log.error("An error occured during Google Sheets reading", e);
        }
    }

    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = GoogleCredential
                .fromStream(SheetsServiceUtil.class.getResourceAsStream("/google-api-service-account.json"))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Beacon Südtirol").build();
    }

    @Transactional(rollbackFor = Exception.class)
    private Info updateInfo(String beaconId, List<Object> row) {
        Info info;
        try {
            info = repository.findById(beaconId).orElseThrow(InfoNotFoundException::new);
        } catch (InfoNotFoundException e) {
            info = new Info();
            info.setId(beaconId);
        }

        applyRowData(info, row);

        return info;
    }

    @Transactional(rollbackFor = Exception.class)
    private Info createInfo(Sheets sheetService, List<Object> row) {
        Info info = new Info();
        info.setId(generateBeaconId());
        info.setUuid(beaconSuedtirolConfiguration.getUuid());
        info.setNamespace(beaconSuedtirolConfiguration.getNamespace());

        //TODO generate and set unused [major, minor] and [instanceId]

        applyRowData(info, row);

        //TODO write beaconId back to sheet, throw exception on failure to perform rollback

        return info;
    }

    private void applyRowData(Info info, List<Object> row) {
        info.setName((String)row.get(1));
        info.setWebsite((String)row.get(7));
        info.setAddress((String)row.get(10));
        info.setLocation((String)row.get(11));
        info.setCap((String)row.get(12));
        info.setLatitude(Float.parseFloat((String)row.get(13)));
        info.setLongitude(Float.parseFloat((String)row.get(14)));
        info.setFloor((String)row.get(15));

        repository.save(info);
    }

    private String generateBeaconId() {
        RandomString randomString = new RandomString(6);
        String beaconId = randomString.nextString();

        try {
            repository.findById(beaconId).orElseThrow(InfoNotFoundException::new);
            return generateBeaconId();
        } catch (InfoNotFoundException e) {
            return beaconId;
        }
    }
}