package it.bz.beacon.api.controller;

import it.bz.beacon.api.db.model.NetworkProblem;
import it.bz.beacon.api.model.Beacon;
import it.bz.beacon.api.service.INetworkProblemService;
import it.bz.beacon.api.service.IBeaconService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/beacons")
public class BeaconController {

    @Autowired
    private IBeaconService service;

    @Autowired
    private INetworkProblemService networkProblemService;

    @ApiOperation(value = "View a list of available beacons")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Beacon> getList() {
        return service.findAll();
    }

    @ApiOperation(value = "View a list of available beacons with network problems")
    @RequestMapping(method = RequestMethod.GET, value = "/withNetworkProblems", produces = "application/json")
    public List<Beacon> getListWithNetworkProblems() {
        List<Long> ids = new ArrayList<>();
        for (NetworkProblem networkProblem : networkProblemService.findAll()) {
            if (!ids.contains(networkProblem.getBeaconId())) {
                ids.add(networkProblem.getBeaconId());
            }
        }

        return service.findAllWithIds(ids);
    }

    @ApiOperation(value = "Search a beacon with an ID")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Beacon get(@PathVariable long id) {
        return service.find(id);
    }

    @ApiOperation(value = "Create a beacon")
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public List<Beacon> create(@RequestBody String orderId) {
        return service.createByOrder(orderId);
    }

    @ApiOperation(value = "Update a beacon")
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = "application/json")
    public Beacon update(@PathVariable long id, @RequestBody Beacon beacon) {
        return service.update(id, beacon);
    }
}
