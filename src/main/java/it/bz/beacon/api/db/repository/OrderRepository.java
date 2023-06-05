// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.OrderData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderData, String> {
    @Query("SELECT o FROM OrderData o WHERE o.orderSymbol IS NOT NULL ORDER BY zoneCode, zoneId")
    List<OrderData> findAllWithOrderSymbol();

    List<OrderData> findAllByOrderSymbolOrderByZoneCodeAscZoneIdAsc(String orderSymbol);

    @Query("SELECT coalesce(max(o.zoneId), 0) FROM OrderData o WHERE o.zoneCode = ?1")
    Integer getMaxZoneId(String zoneCode);

    @Query("SELECT coalesce(max(o.orderSymbol), 'A') FROM OrderData o")
    String getMaxOrderSymbol();
}