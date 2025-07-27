package controllers;

import lombok.RequiredArgsConstructor;
import models.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.OrderServiceV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CheckoutController {

    private final OrderServiceV2 orderServiceV2;

    /**
     * API để tạo order từ cart - gọi trước khi chuyển đến VNPay
     */
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrderFromCart(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("📦 Creating order from cart request:");
            System.out.println("Request data: " + requestData);

            // Extract data from request
            @SuppressWarnings("unchecked")
            List<Integer> productIds = (List<Integer>) requestData.get("productIds");
            @SuppressWarnings("unchecked")
            List<Integer> quantities = (List<Integer>) requestData.get("quantities");

            @SuppressWarnings("unchecked")
            Map<String, String> deliveryInfo = (Map<String, String>) requestData.get("deliveryInfo");
            Boolean express = (Boolean) requestData.getOrDefault("express", false);

            // Check if user is authenticated
            Integer userId = (Integer) requestData.get("userId");
            Integer deliveryId = (Integer) requestData.get("deliveryId");

            System.out.println("🔍 Authentication check:");
            System.out.println("  - UserId: " + userId);
            System.out.println("  - DeliveryId: " + deliveryId);
            System.out.println("  - Is authenticated: " + (userId != null));

            // Validate required fields
            if (productIds == null || quantities == null || deliveryInfo == null) {
                System.err.println("❌ Missing required fields");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Missing required fields: productIds, quantities, deliveryInfo");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (productIds.size() != quantities.size()) {
                System.err.println("❌ Mismatched productIds and quantities arrays");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "productIds and quantities arrays must have same length");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Extract delivery info
            String customerEmail = deliveryInfo.get("email");
            String customerFullName = deliveryInfo.get("name");
            String customerPhone = deliveryInfo.get("phone");
            String deliveryAddress = deliveryInfo.get("address");
            String deliveryProvince = deliveryInfo.get("province");

            if (customerEmail == null || customerFullName == null || customerPhone == null ||
                    deliveryAddress == null || deliveryProvince == null) {
                System.err.println("❌ Missing delivery information fields");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Missing delivery information fields");
                errorResponse.put("received", deliveryInfo);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            System.out.println("✅ Input validation passed");
            System.out.println("  - Product IDs: " + productIds);
            System.out.println("  - Quantities: " + quantities);
            System.out.println("  - Express: " + express);

            Order order;

            // Create order based on authentication status
            if (userId != null) {
                // Authenticated user - create user order
                System.out.println("👤 Creating order for authenticated user: " + userId);
                System.out.println("  - Customer: " + customerFullName + " (" + customerEmail + ")");
                System.out.println("  - DeliveryId: " + deliveryId);

                order = orderServiceV2.createUserOrder(
                        productIds,
                        quantities,
                        userId,
                        deliveryId,
                        express,
                        customerEmail,
                        customerFullName,
                        customerPhone,
                        deliveryAddress,
                        deliveryProvince);
            } else {
                // Guest user - create guest order
                System.out.println("🏃 Creating guest order");
                System.out.println("  - Customer: " + customerFullName + " (" + customerEmail + ")");

                order = orderServiceV2.createGuestOrder(
                        productIds,
                        quantities,
                        customerEmail,
                        customerFullName,
                        customerPhone,
                        deliveryAddress,
                        deliveryProvince,
                        express);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "orderId", order.getOrderId(),
                    "totalAmount", order.getTotalAmount(),
                    "subtotalAmount", order.getSubtotalAmount(),
                    "shippingFees", order.getShippingFees(),
                    "customerEmail", order.getCustomerEmail(),
                    "customerFullName", order.getCustomerFullName(),
                    "orderItems", order.getOrderItems().size()));
            response.put("message", "Order created successfully");

            System.out.println("✅ Order created successfully: " + order.getOrderId());
            System.out.println("  - Total amount: " + order.getTotalAmount());
            System.out.println("  - Order items: " + order.getOrderItems().size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to create order: " + e.getMessage());
            errorResponse.put("errorType", e.getClass().getSimpleName());

            // Include more detailed error information for debugging
            if (e.getCause() != null) {
                errorResponse.put("rootCause", e.getCause().getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API để lấy thông tin order theo ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Integer orderId) {
        try {
            // This would need to be implemented in OrderServiceV2
            // For now, return basic info
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order found");
            response.put("orderId", orderId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Order not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Test endpoint để kiểm tra API hoạt động
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testCheckoutAPI() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Checkout API is working");
            response.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
