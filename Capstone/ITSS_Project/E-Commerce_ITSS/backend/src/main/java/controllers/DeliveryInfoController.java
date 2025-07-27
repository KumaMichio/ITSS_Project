package controllers;

import models.DeliveryInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.DeliveryInfoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/delivery-info")
public class DeliveryInfoController {
    @Autowired
    private DeliveryInfoService deliveryInfoService;

    @GetMapping
    public List<DeliveryInformation> getAllDeliveryInfos() {
        return deliveryInfoService.getAllDeliveryInfos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryInformation> getDeliveryInfoById(@PathVariable int id) {
        Optional<DeliveryInformation> deliveryInfo = deliveryInfoService.getDeliveryInfoById(id);
        return deliveryInfo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public DeliveryInformation addDeliveryInfo(@RequestBody DeliveryInformation deliveryInfo) {
        return deliveryInfoService.addDeliveryInfo(deliveryInfo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryInformation> updateDeliveryInfo(@PathVariable int id, @RequestBody DeliveryInformation deliveryInfo) {
        DeliveryInformation updatedDeliveryInfo = deliveryInfoService.updateDeliveryInfo(id, deliveryInfo);
        if (updatedDeliveryInfo != null) {
            return ResponseEntity.ok(updatedDeliveryInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryInfo(@PathVariable int id) {
        deliveryInfoService.deleteDeliveryInfo(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/user/{userId}")
    public List<DeliveryInformation> getDeliveryInfoByUserId(@PathVariable int userId) {
        return deliveryInfoService.getAllDeliveryInfoByUserId(userId);
    }
}
