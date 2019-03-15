package it.bz.beacon.api.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.db.model.OrderData;
import it.bz.beacon.api.model.BeaconOrder;
import it.bz.beacon.api.service.order.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/orders")
public class OrderController {

    @Autowired
    private IOrderService service;

    @ApiOperation(value = "View a list of all orders", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public List<BeaconOrder> getList() {
        return service.findAll();
    }

    @ApiOperation(value = "Search a order by order symbol", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/{orderSymbol}", produces = "application/json")
    public BeaconOrder get(@PathVariable String orderSymbol) {
        return service.find(orderSymbol);
    }

    @ApiOperation(value = "Create an order for all unordered beacons", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public BeaconOrder create() {
        return service.create();
    }
}
