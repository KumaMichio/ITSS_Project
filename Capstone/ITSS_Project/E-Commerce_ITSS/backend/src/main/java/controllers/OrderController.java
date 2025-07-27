package controllers;

import dtos.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import services.OrderService;
import services.OrderServiceV2;
import models.Order;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderServiceV2 orderServiceV2;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable int id) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(id);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Order> changeOrderStatus(@PathVariable int id) {
        Order updatedOrder = orderService.updateOrderStatus(id);
        if (updatedOrder != null)
            return ResponseEntity.ok(updatedOrder);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update order status");
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createRegularOrder(@RequestParam List<Integer> orderProductIds,
            @RequestParam int userId) {
        Order order = orderService.createRegularOrder(orderProductIds, userId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create-by-delivery-id")
    public ResponseEntity<Order> createRegularOrderWithDeliveryId(@RequestParam List<Integer> orderProductIds,
            @RequestParam int deliveryId) {
        Order order = orderService.createRegularOrderWithDeliveryId(orderProductIds, deliveryId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create-express-order")
    public ResponseEntity<Order> createExpressOrder(@RequestParam List<Integer> orderProductIds,
            @RequestParam int deliveryId) {
        Order order = orderService.createExpressOrder(orderProductIds, deliveryId);
        return ResponseEntity.ok(order);
    }

    /**
     * New V2 API for creating user orders
     */
    @PostMapping("/v2/create-user-order")
    public ResponseEntity<Order> createUserOrderV2(@RequestBody CreateUserOrderRequest request) {
        try {
            Order order = orderServiceV2.createUserOrder(
                    request.getProductIds(),
                    request.getQuantities(),
                    request.getUserId(),
                    request.getDeliveryId(),
                    request.isExpress(),
                    "customer@email.com", // Default customer info
                    "Customer Name",
                    "0123456789",
                    "Default Address",
                    "Default Province");
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Mark order as paid
     */
    @PutMapping("/v2/{orderId}/mark-paid")
    public ResponseEntity<Order> markOrderAsPaid(
            @PathVariable int orderId,
            @RequestParam String transactionId,
            @RequestParam(defaultValue = "BANK_TRANSFER") String paymentMethod,
            @RequestParam(defaultValue = "00") String vnpResponseCode) {
        try {
            Order order = orderServiceV2.markOrderAsPaid(orderId, transactionId, paymentMethod, vnpResponseCode);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Request DTOs
    public static class CreateUserOrderRequest {
        private List<Integer> productIds;
        private List<Integer> quantities;
        private int userId;
        private Integer deliveryId;
        private boolean express;

        // Getters and setters
        public List<Integer> getProductIds() {
            return productIds;
        }

        public void setProductIds(List<Integer> productIds) {
            this.productIds = productIds;
        }

        public List<Integer> getQuantities() {
            return quantities;
        }

        public void setQuantities(List<Integer> quantities) {
            this.quantities = quantities;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public Integer getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(Integer deliveryId) {
            this.deliveryId = deliveryId;
        }

        public boolean isExpress() {
            return express;
        }

        public void setExpress(boolean express) {
            this.express = express;
        }
    }

}
