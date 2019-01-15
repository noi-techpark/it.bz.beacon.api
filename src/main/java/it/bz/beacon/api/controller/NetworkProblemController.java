package it.bz.beacon.api.controller;

import it.bz.beacon.api.db.model.NetworkProblem;
import it.bz.beacon.api.service.IBeaconService;
import it.bz.beacon.api.service.INetworkProblemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/beacons/{beaconId}/network-problems")
public class NetworkProblemController {

    @Autowired
    private IBeaconService beaconService;

    @Autowired
    private INetworkProblemService service;

    @ApiOperation(value = "View a list of available network problems")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<NetworkProblem> getList(@PathVariable long beaconId) {
        return service.findAllForBeacon(beaconService.find(beaconId));
    }

    @ApiOperation(value = "Search a network problem with an ID")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public NetworkProblem get(@PathVariable long beaconId, @PathVariable long id) {
        return service.findForBeacon(beaconService.find(beaconId), id);
    }

    @ApiOperation(value = "Create a network problem")
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public NetworkProblem create(@PathVariable long beaconId, @RequestBody NetworkProblem networkProblem) {
        return service.createForBeacon(beaconService.find(beaconId), networkProblem);
    }

    @ApiOperation(value = "Update a network problem")
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = "application/json")
    public NetworkProblem update(@PathVariable long beaconId, @PathVariable long id, @RequestBody NetworkProblem networkProblem) {
        return service.updateForBeacon(beaconService.find(beaconId), id, networkProblem);
    }

    @ApiOperation(value = "Delete a network problem")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable long beaconId, @PathVariable long id) {
        return service.deleteForBeacon(beaconService.find(beaconId), id);
    }
}
