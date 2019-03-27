package it.bz.beacon.api.controller.admin;

import com.opencsv.CSVWriter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import it.bz.beacon.api.model.BeaconOrder;
import it.bz.beacon.api.model.BeaconOrderData;
import it.bz.beacon.api.service.order.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
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

    @ApiOperation(value = "Search a order by order symbol", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.GET, value = "/csv/{orderSymbol}", produces = "application/json")
    public ResponseEntity<Resource> downloadCsv(@PathVariable String orderSymbol, HttpServletRequest request) {
        BeaconOrder beaconOrder = service.find(orderSymbol);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            CSVWriter writer = new CSVWriter(new BufferedWriter(new OutputStreamWriter(out)));
            String[] header = new String[] {
                    "Profile",
                    "Proximity UUID",
                    "Major",
                    "Minor",
                    "Namespace ID",
                    "Instance ID",
                    "URL",
                    "Interval",
                    "TX Power",
                    "Custom Name"
            };
            writer.writeNext(header);

            List<BeaconOrderData> beacons = beaconOrder.getBeacons();
            beacons.sort(Comparator.comparing(orderData -> String.format("%s%s#%s", orderData.getZoneCode().substring(0, 3),
                    String.format("%04d", orderData.getZoneId()), orderData.getBeaconId())));

            for (BeaconOrderData orderData : beacons) {
                String[] line = new String[] {
                        "iBeacon",
                        orderData.getUuid().toString(),
                        String.format("%d", orderData.getMajor()),
                        String.format("%d", orderData.getMinor()),
                        orderData.getNamespace(),
                        orderData.getInstanceId(),
                        String.format("https://bs.bz.it/%s", orderData.getBeaconId()),
                        String.format("%d", 350),
                        String.format("%d", 3),
                        String.format("%s%s#%s", orderData.getZoneCode().substring(0, 3),
                                String.format("%04d", orderData.getZoneId()), orderData.getBeaconId())
                };
                writer.writeNext(line);
            }
            writer.close();
        } catch (IOException e) {

        }
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        String fileName = String.format("order_%s.csv", orderSymbol);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @ApiOperation(value = "Create an order for all unordered beacons", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public BeaconOrder create() {
        return service.create();
    }
}
