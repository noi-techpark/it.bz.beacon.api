// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.service.beacon;

import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.exception.auth.InsufficientRightsException;
import it.bz.beacon.api.exception.db.BeaconNotFoundException;
import it.bz.beacon.api.exception.order.NoBeaconsToOrderException;
import it.bz.beacon.api.exception.order.NoGroupToOrderException;
import it.bz.beacon.api.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.api.model.BeaconUpdate;
import it.bz.beacon.api.model.ManufacturerOrder;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBeaconService {
    List<Beacon> findAll();

    List<Beacon> findAllByGroupId(Long groupId);
    List<Beacon> findAllWithIds(List<String> ids);
    Beacon find(String id) throws BeaconNotFoundException;

    List<Beacon> createByOrder(ManufacturerOrder order) throws NoGroupToOrderException,
            InsufficientRightsException, BeaconNotFoundException, NoBeaconsToOrderException;
    Beacon update(String id, BeaconUpdate beaconUpdate) throws BeaconNotFoundException;
    ResponseEntity<?> delete(String id) throws BeaconNotFoundException;
    Beacon updateBatteryLevel(String id, BeaconBatteryLevelUpdate batteryLevelUpdate) throws BeaconNotFoundException;
}
