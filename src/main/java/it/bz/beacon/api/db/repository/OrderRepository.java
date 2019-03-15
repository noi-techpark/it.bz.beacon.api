package it.bz.beacon.api.db.repository;

import it.bz.beacon.api.db.model.OrderData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderData, String> {
    List<OrderData> findAllByOrderSymbol(String orderSymbol);
}