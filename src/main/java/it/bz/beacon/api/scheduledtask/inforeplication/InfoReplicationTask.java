package it.bz.beacon.api.scheduledtask.inforeplication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.config.InfoImporterTaskConfiguration;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.model.OrderData;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.db.repository.OrderRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.scheduledtask.inforeplication.error.GeneralError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.ParseError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.RowError;
import it.bz.beacon.api.scheduledtask.inforeplication.error.SheetError;
import it.bz.beacon.api.scheduledtask.inforeplication.value.EddystoneValue;
import it.bz.beacon.api.scheduledtask.inforeplication.value.EddystoneValueGenerator;
import it.bz.beacon.api.scheduledtask.inforeplication.value.iBeaconValue;
import it.bz.beacon.api.service.beacon.BeaconService;
import it.bz.beacon.api.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class InfoReplicationTask {

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private InfoImporterTaskConfiguration importerConfiguration;

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BeaconService beaconService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private it.bz.beacon.api.scheduledtask.inforeplication.value.iBeaconValueGenerator iBeaconValueGenerator;

    @Autowired
    private EddystoneValueGenerator eddystoneValueGenerator;

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
            Spreadsheet spreadsheet = sheetService.spreadsheets().get(importerConfiguration.getSpreadSheetId()).execute();

            List<Zone> zones = new ArrayList<>();
            List<SheetError> sheetErrors = new ArrayList<>();

            BatchGetValuesResponse zoneResponse = sheetService.spreadsheets().values()
                    .batchGet(importerConfiguration.getSpreadSheetId()).setRanges(Collections.singletonList("Zones"))
                    .execute();


            for (ValueRange valueRange : zoneResponse.getValueRanges()) {
                List<List<Object>> rows = valueRange.getValues();
                if (rows.size() > 1) {
                    for (int i = 1; i < rows.size(); i++) {
                        zones.add(new Zone((List<String>)(Object)rows.get(i)));
                    }
                }
            }

            for (Zone zone : zones) {
                SheetError sheetError = new SheetError(zone.getSheetName());
                BatchGetValuesResponse sheetResponse = sheetService.spreadsheets().values()
                        .batchGet(importerConfiguration.getSpreadSheetId()).setRanges(Collections.singletonList(zone.getSheetName()))
                        .execute();

                for (ValueRange valueRange : sheetResponse.getValueRanges()) {
                    List<List<Object>> rows = valueRange.getValues();
                    for (int i = 1; i < rows.size(); i++) {
                        List<Object> row = rows.get(i);
                        String beaconId = (String) row.get(0);
                        RowError rowError = new RowError(i + 1);

                        try {
                            InfoData infoData = new InfoData(row);
                            if (!infoData.isValid()) {
                                rowError.setErrors(infoData.getErrors());
                            }

                            if (beaconId != null && beaconId.trim().length() > 0) {
                                update(beaconId, infoData);
                            } else {
                                create(sheetService, zone, i + 1,infoData);
                            }
                        } catch (Exception e) {
                            rowError.addError(new GeneralError(String.format("An exception occurred: %s", e.getMessage())));
                        }

                        if (rowError.hasErrors()) {
                            sheetError.addError(rowError);
                        }
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
                //TODO send to zone manager
                message.setTo("dev@rolmail.net");
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

    @Transactional(rollbackFor = DbWriteException.class)
    private void update(String beaconId, InfoData infoData) {
        Info info;
        try {
            info = infoRepository.findById(beaconId).orElseThrow(InfoNotFoundException::new);
        } catch (InfoNotFoundException e) {
            info = new Info();
            info.setId(beaconId);
        }

        applyRowData(info, infoData);
    }

    @Transactional(rollbackFor = SheetWriteException.class)
    private void create(Sheets sheetService, Zone zone, int rowIndex, InfoData infoData) {
        Info info = new Info();
        info.setId(generateBeaconId());
        info.setNamespace(beaconSuedtirolConfiguration.getNamespace());

        iBeaconValue iBeaconValue = iBeaconValueGenerator.randomValue();
        info.setUuid(iBeaconValue.getUuid());
        info.setMajor(iBeaconValue.getMajor());
        info.setMinor(iBeaconValue.getMinor());

        EddystoneValue eddystoneValue = eddystoneValueGenerator.randomValue();
        info.setNamespace(eddystoneValue.getNamespace());
        info.setInstanceId(eddystoneValue.getInstanceId());

        applyRowData(info, infoData);

        try {
            OrderData order = new OrderData();
            order.setId(info.getId());
            order.setInfo(info);
            order.setZoneCode(zone.getCode());
            order.setZoneId(orderRepository.getNextZoneId(zone.getCode()));
            orderRepository.save(order);
        } catch (Exception e) {
            throw new DbWriteException(e);
        }

        ValueRange body = new ValueRange().setValues(Collections.singletonList(Collections.singletonList(info.getId())));
        try {
            UpdateValuesResponse writeResponse = sheetService.spreadsheets().values()
                    .update(importerConfiguration.getSpreadSheetId(), String.format("'%s'!A%d", zone.getSheetName(), rowIndex), body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            throw new SheetWriteException(e);
        }
    }

    private void applyRowData(Info info, InfoData infoData) throws DbWriteException {
        info.setName(infoData.getName());
        info.setWebsite(infoData.getWebsite());
        info.setAddress(infoData.getAddress());
        info.setLocation(infoData.getLocation());
        info.setCap(infoData.getCap());
        info.setLatitude(infoData.getLatitude());
        info.setLongitude(infoData.getLongitude());
        info.setFloor(infoData.getFloor());

        try {
            infoRepository.save(info);
        } catch (Exception e) {
            throw new DbWriteException(e);
        }
    }

    private String generateBeaconId() {
        RandomString randomString = new RandomString(6, RandomString.ALPHANUMERIC);
        String beaconId = randomString.nextString();

        try {
            infoRepository.findById(beaconId).orElseThrow(InfoNotFoundException::new);
            return generateBeaconId();
        } catch (InfoNotFoundException e) {
            return beaconId;
        }
    }
}