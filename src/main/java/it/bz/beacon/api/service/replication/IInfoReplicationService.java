package it.bz.beacon.api.service.replication;

import com.google.api.services.sheets.v4.Sheets;
import it.bz.beacon.api.scheduledtask.inforeplication.InfoData;
import it.bz.beacon.api.scheduledtask.inforeplication.Zone;

public interface IInfoReplicationService {
    void create(Sheets sheetService, Zone zone, int rowIndex, InfoData infoData);
    void update(String beaconId, InfoData infoData, Zone zone);
}
