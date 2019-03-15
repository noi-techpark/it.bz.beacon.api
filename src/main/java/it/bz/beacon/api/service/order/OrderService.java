package it.bz.beacon.api.service.order;

import it.bz.beacon.api.db.model.OrderData;
import it.bz.beacon.api.db.repository.InfoRepository;
import it.bz.beacon.api.db.repository.OrderRepository;
import it.bz.beacon.api.exception.db.OrderSymbolNotFoundException;
import it.bz.beacon.api.model.BeaconOrder;
import it.bz.beacon.api.model.BeaconOrderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private InfoRepository infoRepository;

    @Override
    public List<BeaconOrder> findAll() {
        Map<String, BeaconOrder> beaconOrderMap = new HashMap<>();

        List<OrderData> orderDataList = repository.findAll();

        for (OrderData orderData : orderDataList) {
            if (!beaconOrderMap.containsKey(orderData.getOrderSymbol())) {
                beaconOrderMap.put(orderData.getOrderSymbol(), new BeaconOrder(orderData.getOrderSymbol()));
            }
            BeaconOrder beaconOrder = beaconOrderMap.get(orderData.getOrderSymbol());
            beaconOrder.addBeacon(new BeaconOrderData(orderData));
        }

        return new ArrayList<>(beaconOrderMap.values());
    }

    @Override
    @Transactional
    public BeaconOrder find(String orderSymbol) {
        List<OrderData> orderDataList = repository.findAllByOrderSymbol(orderSymbol);

        if (orderDataList.size() <= 0) {
            throw new OrderSymbolNotFoundException();
        }

        BeaconOrder beaconOrder = new BeaconOrder(orderSymbol);
        for (OrderData orderData : orderDataList) {
            beaconOrder.addBeacon(new BeaconOrderData(orderData));
        }

        return beaconOrder;
    }

    @Override
    public BeaconOrder create() {
        List<OrderData> orderDataList = repository.findAllByOrderSymbol(null);

        //TODO generate symbol
        String orderSymbol = "TEST";

        BeaconOrder beaconOrder = new BeaconOrder(orderSymbol);

        for (OrderData orderData : orderDataList) {
            orderData.setOrderSymbol(orderSymbol);
            repository.save(orderData);
            beaconOrder.addBeacon(new BeaconOrderData(orderData));
        }

        return beaconOrder;
    }
}
