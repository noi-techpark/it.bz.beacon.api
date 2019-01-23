package it.bz.beacon.api.controller.beacon;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.BeaconImage;
import it.bz.beacon.api.model.BaseMessage;
import it.bz.beacon.api.service.image.FileStorageService;
import it.bz.beacon.api.service.beacon.IBeaconDataService;
import it.bz.beacon.api.service.image.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/beacons/{beaconId}/images")
public class ImageController {

    @Autowired
    private IBeaconDataService beaconDataService;

    @Autowired
    private IImageService service;

    @Autowired
    private FileStorageService fileStorageService;

    @ApiOperation(value = "View a list of available beacons", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<BeaconImage> getList(@PathVariable long beaconId) {
        beaconDataService.find(beaconId);

        return service.findAll();
    }

    @ApiOperation(value = "Create an image", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public BeaconImage create(@PathVariable long beaconId, @RequestParam("file") MultipartFile file) {
        BeaconData beaconData = beaconDataService.find(beaconId);

        return service.create(beaconData, fileStorageService.storeFile(file));
    }

    @ApiOperation(value = "Delete an image", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public BaseMessage delete(@PathVariable long beaconId, @PathVariable long id) {
        beaconDataService.find(beaconId);

        return service.delete(id);
    }
}
