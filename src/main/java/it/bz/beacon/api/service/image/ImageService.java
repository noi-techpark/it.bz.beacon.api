package it.bz.beacon.api.service.image;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.BeaconImage;
import it.bz.beacon.api.db.repository.BeaconImageRepository;
import it.bz.beacon.api.exception.db.BeaconImageNotFoundException;
import it.bz.beacon.api.model.BaseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Component
public class ImageService implements IImageService {

    @Autowired
    private BeaconImageRepository repository;

    @Override
    public List<BeaconImage> findAll(BeaconData beaconData) {
        return repository.findAllByBeaconId(beaconData.getId());
    }

    @Override
    public BeaconImage find(long id) throws BeaconImageNotFoundException {
        return repository.findById(id).orElseThrow(BeaconImageNotFoundException::new);
    }

    @Override
    public BeaconImage create(BeaconData beaconData, String fileName) {
//        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/uploads/")
//                .path(fileName)
//                .toUriString();

        BeaconImage image = new BeaconImage();
        image.setBeaconId(beaconData.getId());
        image.setFileName(fileName);

        return repository.save(image);
    }

    @Override
    public BaseMessage delete(long id) throws BeaconImageNotFoundException {
        return repository.findById(id).map(
                beaconImage -> {
                    repository.delete(beaconImage);

                    return new BaseMessage("Image deleted");
                }
        ).orElseThrow(BeaconImageNotFoundException::new);
    }
}
