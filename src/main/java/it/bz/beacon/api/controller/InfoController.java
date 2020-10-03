package it.bz.beacon.api.controller;

import io.swagger.annotations.ApiOperation;
import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.service.info.IInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/infos")
public class InfoController {

    @Autowired
    private IInfoService service;

    @ApiOperation(value = "View a list of all infos")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<Info> getList(@RequestParam(value = "updatedAfter", required = false) Long updatedAfter) {
        if (updatedAfter != null) {
            return service.findAllAfter(new Date(updatedAfter));
        }
        return service.findAll();
    }

    @ApiOperation(value = "Search a info with an ID")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public Info get(@PathVariable String id) {
        return service.findByBeaconId(id);
    }

    @ApiOperation(value = "Search a info with an Eddystone instanceId value")
    @RequestMapping(method = RequestMethod.GET, value = "/eddystone/{instanceId}", produces = "application/json")
    public Info getEddyStone(@PathVariable String instanceId) {
        return service.findByInstanceId(instanceId);
    }

    @ApiOperation(value = "Search a info with an iBeacon major and minor value")
    @RequestMapping(method = RequestMethod.GET, value = "/ibeacon/{major}/{minor}", produces = "application/json")
    public Info getiBeacon(@PathVariable int major, @PathVariable int minor) {
        return service.findByMajorMinor(major, minor);
    }
}
