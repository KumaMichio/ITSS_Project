package controllers;

import models.ShippingMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.ShippingMethodService;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-method")
public class ShippingMethodController {
    @Autowired
    private ShippingMethodService shippingMethodService;

    @GetMapping()
    public List<ShippingMethod> getProductByTitleContaining() {
        return shippingMethodService.getShippingMethods();
    }
}
