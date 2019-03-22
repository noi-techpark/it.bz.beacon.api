package it.bz.beacon.api.scheduledtask.inforeplication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.config.InfoImporterTaskConfiguration;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.scheduledtask.inforeplication.error.GeneralError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.ParseError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.RowError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.SheetError;
import it.bz.beacon.api.service.beacon.BeaconService;
import it.bz.beacon.api.service.replication.IInfoReplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Service
public class InfoReplicationTask {

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private InfoImporterTaskConfiguration importerConfiguration;

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private IInfoReplicationService replicationService;

    private static final Logger log = LoggerFactory.getLogger(InfoReplicationTask.class);

    @Scheduled(fixedDelayString = "${it.bz.beacon.task.infoimport.delay:10800000}")
    public void startImport() {
        if (importerConfiguration.isEnabled()) {
            log.info("Starting sheet import...");
            try {
                replicateGoogleSheet();
                updateBeaconPackageData();
            } catch (Exception e) {
                log.error("An unexcpected error occurred: " + e.getMessage());
            }
        }
    }

    private void updateBeaconPackageData() {
        List<Beacon> beacons = beaconService.findAll();
        for (Beacon beacon : beacons) {
            try {
                Info info = infoRepository.findById(beacon.getId()).orElseThrow(InfoNotFoundException::new);
                info.setUuid(beacon.getUuid());
                info.setMajor(beacon.getMajor());
                info.setMinor(beacon.getMinor());
                info.setNamespace(beacon.getNamespace());
                info.setInstanceId(beacon.getInstanceId());

                infoRepository.save(info);
            } catch (InfoNotFoundException e) {
                //TODO handle
            }
        }
    }

    private void replicateGoogleSheet() {
        try {
            Sheets sheetService = getSheetsService();

            Map<String, Zone> zones = new HashMap<>();
            List<SheetError> sheetErrors = new ArrayList<>();

            BatchGetValuesResponse zoneResponse = sheetService.spreadsheets().values()
                    .batchGet(importerConfiguration.getSpreadSheetId()).setRanges(Collections.singletonList("Zones"))
                    .execute();


            for (ValueRange valueRange : zoneResponse.getValueRanges()) {
                List<List<Object>> rows = valueRange.getValues();
                if (rows.size() > 1) {
                    for (int i = 1; i < rows.size(); i++) {
                        try {
                            Zone zone = new Zone(rows.get(i));
                            zones.put(zone.getSheetName(), zone);
                        } catch (InvalidZoneException e) {
                            log.error(String.format("Zone [ %d ]: %s", i + 1, e.getMessage()));
                        }
                    }
                }
            }

            List<String> ranges = new ArrayList<>(zones.keySet());
            BatchGetValuesResponse rangesResponse = sheetService.spreadsheets().values()
                    .batchGet(importerConfiguration.getSpreadSheetId()).setRanges(ranges)
                    .execute();

            List<ValueRange> zoneRanges = rangesResponse.getValueRanges();
            zoneRanges.sort(Comparator.comparing(ValueRange::getRange));
            for (ValueRange zoneRange : zoneRanges) {
                String sheetName = zoneRange.getRange().split("'")[1];
                Zone zone = zones.get(sheetName);
                if (zone == null) {
                    continue;
                }

                SheetError sheetError = new SheetError(zone);

                List<List<Object>> rows = zoneRange.getValues();
                for (int i = 1; i < rows.size(); i++) {
                    List<Object> row = rows.get(i);
                    if (row.size() <= 0) {
                        continue;
                    }

                    String beaconId = (String) row.get(0);
                    RowError rowError = new RowError(i + 1);

                    try {
                        InfoData infoData = new InfoData(row);
                        if (!infoData.isValid()) {
                            rowError.setErrors(infoData.getErrors());
                        }

                        if (beaconId != null && beaconId.trim().length() > 0) {
                            replicationService.update(beaconId, infoData, zone);
                        } else {
                            try {
                                replicationService.create(sheetService, zone, i + 1, infoData);
                            } catch (SheetWriteException e) {
                                if (e.getCause() instanceof GoogleJsonResponseException) {
                                    GoogleJsonResponseException gex = (GoogleJsonResponseException)e.getCause();
                                    if (gex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                                        try {
                                            //Sleep for 200 seconds to avoid to exceed free Google sheet quota
                                            log.warn("Google sheet quota exceeded, sleeping for 200 seconds before retry...");
                                            Thread.sleep(200 * 1000);
                                            replicationService.create(sheetService, zone, i + 1, infoData);
                                        } catch (InterruptedException ie) {
                                            throw new SheetWriteException(ie);
                                        }
                                    } else {
                                        throw e;
                                    }
                                } else {
                                    throw e;
                                }
                            }
                        }
                    } catch (Exception e) {
                        rowError.addError(new GeneralError(String.format("An exception occurred: %s", e.getMessage())));
                    }

                    if (rowError.hasErrors()) {
                        sheetError.addError(rowError);
                    }
                }

                if (sheetError.hasErrors()) {
                    sheetErrors.add(sheetError);
                }
            }

            notfiyErrors(sheetErrors);

        } catch (IOException | GeneralSecurityException e) {
            log.error("An error occured during Google Sheets reading", e);
        }
    }

    private void notfiyErrors(List<SheetError> sheetErrors) {
        if (sheetErrors.size() > 0) {
            for (SheetError sheetError : sheetErrors) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("There have been found invalid values in some sheet rows for sheet [ %s ].",
                        sheetError.getTitle()));
                sb.append(System.lineSeparator());
                sb.append("Therefore it was not possible to replicate these values to the info database. Other row values have been updated.");
                sb.append(System.lineSeparator());
                sb.append(System.lineSeparator());
                sb.append("Please check the following rows: ");
                sb.append(System.lineSeparator());
                sb.append(System.lineSeparator());

                for (RowError rowError : sheetError.getErrors()) {
                    sb.append(System.lineSeparator());
                    sb.append("\t");
                    sb.append(String.format("Row #%s:", rowError.getIndex()));

                    for (ParseError parseError : rowError.getErrors()) {
                        sb.append(System.lineSeparator());
                        sb.append("\t\t");
                        sb.append(parseError.getMessage());
                    }
                    sb.append(System.lineSeparator());
                }
                sb.append(System.lineSeparator());

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(beaconSuedtirolConfiguration.getIssueEmailFrom());
                message.setTo(sheetError.getZone().getEmail());
                message.setSubject(String.format("Beacon Südtirol - Info sheet replication errors for [ %s ]",
                        sheetError.getTitle()));
                message.setText(sb.toString());

                try {
                    emailSender.send(message);
                } catch (Exception e) {
                    log.error("Email not sent: " + e.getMessage());
                }
            }
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
}