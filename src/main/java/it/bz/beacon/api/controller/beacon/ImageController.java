package it.bz.beacon.api.controller.beacon;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.BeaconImage;
import it.bz.beacon.api.exception.db.BeaconImageNotFoundException;
import it.bz.beacon.api.model.BaseMessage;
import it.bz.beacon.api.service.image.FileStorageService;
import it.bz.beacon.api.service.beacon.IBeaconDataService;
import it.bz.beacon.api.service.image.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    @ApiOperation(value = "View a list of available images for a beacon", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<BeaconImage> getList(@PathVariable String beaconId) {
        BeaconData beaconData = beaconDataService.find(beaconId);

        return service.findAll(beaconData);
    }

    @ApiOperation(value = "Create an image for a beacon", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public BeaconImage create(@PathVariable String beaconId, @RequestParam("file") MultipartFile file) {
        BeaconData beaconData = beaconDataService.find(beaconId);

        return service.create(beaconData, fileStorageService.storeFile(file));
    }

    @ApiOperation(value = "Get an image for a beacon", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "image/*")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String beaconId, @PathVariable long id, HttpServletRequest request) {
        BeaconData beaconData = beaconDataService.find(beaconId);

        BeaconImage beaconImage = service.find(id);

        if (!beaconImage.getBeaconId().equals(beaconData.getId())) {
            throw new BeaconImageNotFoundException();
        }

        Resource resource = fileStorageService.loadAsResource(beaconImage.getFileName());

        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @ApiOperation(value = "Delete an image", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = "application/json")
    public BaseMessage delete(@PathVariable String beaconId, @PathVariable long id) {
        beaconDataService.find(beaconId);

        return service.delete(id);
    }
}
