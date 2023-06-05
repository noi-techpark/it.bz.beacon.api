// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.Beacon;
import it.bz.beacon.api.model.BeaconUpdate;
import it.bz.beacon.api.model.ManufacturerOrder;
import it.bz.beacon.api.service.beacon.IBeaconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/beacons")
public class BeaconController {

    @Autowired
    private IBeaconService service;

    @ApiOperation(value = "View a list of available beacons",
            authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Beacon> getList(@RequestParam(value = "groupId", required = false) Long groupId) {
        if (groupId != null) {
            return service.findAllByGroupId(groupId);
        }

        List<Beacon> result = service.findAll();
        return result;
    }

    @ApiOperation(value = "Search a beacon with an ID", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Beacon get(@PathVariable String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Create a beacon", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.POST, value = "/createByOrder", produces = "application/json")
    public List<Beacon> create(@Valid @RequestBody ManufacturerOrder order) {
        return service.createByOrder(order);
    }

    @ApiOperation(value = "Update a beacon", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}", produces = "application/json")
    public Beacon update(@PathVariable String id, @Valid @RequestBody BeaconUpdate beaconUpdate) {
        return service.update(id, beaconUpdate);
    }
}
