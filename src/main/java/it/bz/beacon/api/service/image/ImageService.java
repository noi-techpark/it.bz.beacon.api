package it.bz.beacon.api.service.image;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.BeaconImage;
import it.bz.beacon.api.db.repository.ImageRepository;
import it.bz.beacon.api.exception.db.ImageNotFoundException;
import it.bz.beacon.api.model.BaseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Component
public class ImageService implements IImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public List<BeaconImage> findAll() {
        return null;
    }

    @Override
    public BeaconImage create(BeaconData beaconData, String fileName) {
        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();

        BeaconImage image = new BeaconImage();
        image.setBeaconId(beaconData.getId());
        image.setUrl(uri);

        return imageRepository.save(image);
    }

    @Override
    public BaseMessage delete(long id) throws ImageNotFoundException {
        return null;
    }
}
