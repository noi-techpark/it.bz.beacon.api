package it.bz.beacon.api.service.replication;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.config.InfoImporterTaskConfiguration;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.db.model.OrderData;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.db.repository.OrderRepository;
import it.bz.beacon.api.exception.db.InfoNotFoundException;
import it.bz.beacon.api.scheduledtask.inforeplication.DbWriteException;
import it.bz.beacon.api.scheduledtask.inforeplication.InfoData;
import it.bz.beacon.api.scheduledtask.inforeplication.SheetWriteException;
import it.bz.beacon.api.scheduledtask.inforeplication.Zone;
import it.bz.beacon.api.scheduledtask.inforeplication.value.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.Collections;

@Component
public class InfoReplicationService implements IInfoReplicationService {

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private InfoImporterTaskConfiguration importerConfiguration;

    @Autowired
    private BeaconIdGenerator beaconIdGenerator;

    @Autowired
    private IBeaconValueGenerator iBeaconValueGenerator;

    @Autowired
    private EddystoneValueGenerator eddystoneValueGenerator;

    @Transactional(rollbackFor = Exception.class)
    public void update(String beaconId, InfoData infoData, Zone zone) {
        Info info;
        try {
            info = infoRepository.findById(beaconId).orElseThrow(InfoNotFoundException::new);
        } catch (InfoNotFoundException e) {
            info = new Info();
            info.setId(beaconId);
        }

        applyRowData(info, infoData);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(Sheets sheetService, Zone zone, int rowIndex, InfoData infoData) {
        Info info = new Info();
        info.setId(beaconIdGenerator.randomValue());
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
            order.setZoneId(orderRepository.getMaxZoneId(zone.getCode()) + 1);
            orderRepository.save(order);
        } catch (Exception e) {
            throw new DbWriteException(e);
        }

        writeIdToSheet(sheetService, zone, info, rowIndex);
    }

    private void writeIdToSheet(Sheets sheetService, Zone zone, Info info, int rowIndex) {
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
}
