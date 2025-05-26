package controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipping-method")
public class ShippingMethodController {
    @Autowired
    private ShippingMethodServiceImpl shippingMethodService;

    @GetMapping()
    public List<ShippingMethod> getProductByTitleContaining() {
        return shippingMethodService.getShippingMethods();
    }
}
