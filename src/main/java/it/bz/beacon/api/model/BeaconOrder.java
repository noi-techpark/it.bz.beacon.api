// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.model;

import it.bz.beacon.api.exception.order.OrderSymbolsNotMatchingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconOrder {
    private String orderSymbol;
    private Map<String, BeaconOrderData> beacons = new HashMap<>();

    public BeaconOrder(String orderSymbol) {
        this.orderSymbol = orderSymbol;
    }

    public String getOrderSymbol() {
        return orderSymbol;
    }

    public List<BeaconOrderData> getBeacons() {
        return new ArrayList<>(beacons.values());
    }

    public void addBeacon(BeaconOrderData beaconOrderData) throws OrderSymbolsNotMatchingException {
        if (!beaconOrderData.getOrderSymbol().equals(orderSymbol)) {
            throw new OrderSymbolsNotMatchingException();
        }

        this.beacons.put(beaconOrderData.getBeaconId(), beaconOrderData);
    }
}
