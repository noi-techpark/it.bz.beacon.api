package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.BeaconImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<BeaconImage, Long> {
}