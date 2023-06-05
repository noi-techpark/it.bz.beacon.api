// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.service.image;

import it.bz.beacon.api.db.model.BeaconData;
import it.bz.beacon.api.db.model.BeaconImage;
import it.bz.beacon.api.exception.db.BeaconImageNotFoundException;
import it.bz.beacon.api.model.BaseMessage;

import java.util.List;

public interface IImageService {
    List<BeaconImage> findAll(BeaconData beaconData);
    BeaconImage find(long id);
    BeaconImage create(BeaconData beaconData, String fileName);
    BaseMessage delete(long id) throws BeaconImageNotFoundException;
}
