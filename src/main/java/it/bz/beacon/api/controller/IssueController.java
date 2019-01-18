package it.bz.beacon.api.controller;

import io.swagger.annotations.ApiOperation;
import it.bz.beacon.api.db.model.IssueSolution;
import it.bz.beacon.api.model.BeaconIssue;
import it.bz.beacon.api.model.IssueCreation;
import it.bz.beacon.api.service.issue.IIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/issues")
public class IssueController {

    @Autowired
    private IIssueService service;

    @ApiOperation(value = "View a list of available issues")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<BeaconIssue> getList(@RequestParam(defaultValue = "false", required = false) boolean onlyUnresolved) {
        return service.findAll(onlyUnresolved);
    }

    @ApiOperation(value = "Search a issue with an ID")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
    public BeaconIssue get(@PathVariable long id) {
        return service.find(id);
    }

    @ApiOperation(value = "Create a issue")
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public BeaconIssue create(@Valid @RequestBody IssueCreation issueCreation) {
        return service.create(issueCreation);
    }

    @ApiOperation(value = "Update a issue")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/resolve", produces = "application/json")
    public BeaconIssue update(@PathVariable long id, @Valid @RequestBody IssueSolution issueSolution) {
        return service.resolve(id, issueSolution);
    }
}
