package controllers;

import lombok.RequiredArgsConstructor;
import models.DeliveryInformation;
import models.ShippingMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repositories.DeliveryInfoRepository;
import repositories.ShippingMethodRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SetupController {

    private final ShippingMethodRepository shippingMethodRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;

    /**
     * Setup default shipping methods
     */
    @PostMapping("/shipping-methods")
    public ResponseEntity<Map<String, Object>> setupDefaultShippingMethods() {
        try {
            // Check if shipping methods already exist
            boolean hasStandard = shippingMethodRepository.findById(1).isPresent();
            boolean hasExpress = shippingMethodRepository.findById(2).isPresent();

            if (!hasStandard) {
                ShippingMethod standard = ShippingMethod.shippingMethodBuilder()
                        .methodName("Standard")
                        .isRush(false)
                        .shippingFees(30000.0)
                        .build();
                shippingMethodRepository.save(standard);
                System.out.println("✅ Created Standard shipping method");
            }

            if (!hasExpress) {
                ShippingMethod express = ShippingMethod.shippingMethodBuilder()
                        .methodName("Express 2H")
                        .isRush(true)
                        .shippingFees(50000.0)
                        .build();
                shippingMethodRepository.save(express);
                System.out.println("✅ Created Express shipping method");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shipping methods setup completed");
            response.put("standardExists", hasStandard);
            response.put("expressExists", hasExpress);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error setting up shipping methods: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to setup shipping methods: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get all shipping methods
     */
    @GetMapping("/shipping-methods")
    public ResponseEntity<Map<String, Object>> getShippingMethods() {
        try {
            var shippingMethods = shippingMethodRepository.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shippingMethods);
            response.put("count", shippingMethods.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to get shipping methods: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Setup default delivery mappings for guest orders
     * delivery_id = 1 for standard shipping
     * delivery_id = 2 for express shipping
     */
    @PostMapping("/delivery-mappings")
    public ResponseEntity<Map<String, Object>> setupDeliveryMappings() {
        try {
            // Check if delivery mappings already exist
            boolean hasStandard = deliveryInfoRepository.findById(1).isPresent();
            boolean hasExpress = deliveryInfoRepository.findById(2).isPresent();

            if (!hasStandard) {
                DeliveryInformation standardMapping = DeliveryInformation.builder()
                        .name("Standard Delivery")
                        .phone("System")
                        .address("System Generated")
                        .province("System")
                        .instruction("Standard shipping mapping")
                        .user(null) // Will be set to system user if needed
                        .build();

                // Since user_id is NOT NULL, we need to handle this differently
                // Let's create without saving for now and handle in service
                System.out.println("⚠️ Cannot create delivery mapping without user - will handle in service");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery mappings setup will be handled in OrderService");
            response.put("standardExists", hasStandard);
            response.put("expressExists", hasExpress);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error setting up delivery mappings: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to setup delivery mappings: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get all delivery mappings
     */
    @GetMapping("/delivery-mappings")
    public ResponseEntity<Map<String, Object>> getDeliveryMappings() {
        try {
            var deliveryMappings = deliveryInfoRepository.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", deliveryMappings);
            response.put("count", deliveryMappings.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to get delivery mappings: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
