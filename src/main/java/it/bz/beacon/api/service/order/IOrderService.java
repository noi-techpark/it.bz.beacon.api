// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.service.order;

import it.bz.beacon.api.model.BeaconOrder;

import java.util.List;

public interface IOrderService {
    List<BeaconOrder> findAll();
    BeaconOrder find(String orderSymbol);
    BeaconOrder create();
}
