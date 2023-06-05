// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.controller.trusted;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.model.BeaconBatteryLevelUpdate;
import it.bz.beacon.api.service.beacon.IBeaconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/trusted/beacons")
public class TrustedBeaconController {

    @Autowired
    private IBeaconService service;

    @ApiOperation(value = "Update battery level of beacon", authorizations = {@Authorization(value = "TrustedAuth")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}/batteryLevel", produces = "application/json")
    public Beacon update(@PathVariable String id, @Valid @RequestBody BeaconBatteryLevelUpdate batteryLevelUpdate) {
        return service.updateBatteryLevel(id, batteryLevelUpdate);
    }
}
