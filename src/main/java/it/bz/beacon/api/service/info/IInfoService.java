// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.service.info;

import it.bz.beacon.api.db.model.Info;
import it.bz.beacon.api.exception.db.InfoNotFoundException;

import java.util.Date;
import java.util.List;

public interface IInfoService {
    List<Info> findAll();
    List<Info> findAllAfter(Date date);
    Info findByBeaconId(String beaconId) throws InfoNotFoundException;
    Info findByInstanceId(String instanceId) throws InfoNotFoundException;
    Info findByMajorMinor(int major, int minor) throws InfoNotFoundException;
}
