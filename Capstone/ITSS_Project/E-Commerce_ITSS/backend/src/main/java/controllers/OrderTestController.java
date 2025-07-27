package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.OrderServiceV2;
import models.Order;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/orders")
public class OrderTestController {

    @Autowired
    private OrderServiceV2 orderService;

    @PostMapping("/test-create")
    public ResponseEntity<?> testCreateOrder(@RequestBody TestOrderRequest request) {
        try {
            // Extract product IDs and quantities
            List<Integer> productIds = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();

            for (TestOrderRequest.ProductItem item : request.getProducts()) {
                productIds.add(item.getProductId());
                quantities.add(item.getQuantity());
            }

            // Create order using OrderServiceV2
            Order order = orderService.createUserOrder(
                    productIds,
                    quantities,
                    request.getUserId(),
                    null, // deliveryId - using null for test
                    false, // isExpress - default to false
                    "test@email.com", // Default test customer info
                    "Test Customer",
                    "0123456789",
                    "Test Address",
                    "Test Province");

            return ResponseEntity.ok(new TestOrderResponse(
                    order.getOrderId(),
                    "Order created successfully",
                    order.getTotalAmount(),
                    order.getOrderItems().size()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Failed to create order: " + e.getMessage()));
        }
    }

    // Request/Response DTOs
    public static class TestOrderRequest {
        private int userId;
        private List<ProductItem> products;

        // Getters and setters
        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public List<ProductItem> getProducts() {
            return products;
        }

        public void setProducts(List<ProductItem> products) {
            this.products = products;
        }

        public static class ProductItem {
            private int productId;
            private int quantity;

            // Getters and setters
            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }
        }
    }

    public static class TestOrderResponse {
        private int orderId;
        private String message;
        private double totalAmount;
        private int itemCount;

        public TestOrderResponse(int orderId, String message, double totalAmount, int itemCount) {
            this.orderId = orderId;
            this.message = message;
            this.totalAmount = totalAmount;
            this.itemCount = itemCount;
        }

        // Getters
        public int getOrderId() {
            return orderId;
        }

        public String getMessage() {
            return message;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public int getItemCount() {
            return itemCount;
        }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
