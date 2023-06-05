// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.BeaconImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeaconImageRepository extends JpaRepository<BeaconImage, Long> {
    List<BeaconImage> findAllByBeaconId(String beaconId);
}