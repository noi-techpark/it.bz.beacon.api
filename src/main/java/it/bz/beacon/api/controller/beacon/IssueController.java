package it.bz.beacon.api.controller.beacon;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.model.BeaconIssue;
import it.bz.beacon.api.service.beacon.IBeaconDataService;
import it.bz.beacon.api.service.issue.IIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("BeaconIssueController")
@RequestMapping("/v1/admin/beacons/{beaconId}/issues")
public class IssueController {

    @Autowired
    private IBeaconDataService beaconDataService;

    @Autowired
    private IIssueService service;

    @ApiOperation(value = "View a list of available issues for the specified beacon ID", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<BeaconIssue> getList(@PathVariable long beaconId, @RequestParam(defaultValue = "false", required = false) boolean onlyUnresolved) {
        BeaconData beaconData = beaconDataService.find(beaconId);
        return service.findAllByBeacon(beaconData, onlyUnresolved);
    }
}
