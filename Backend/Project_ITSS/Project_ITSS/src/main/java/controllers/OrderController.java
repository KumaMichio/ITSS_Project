package controllers;

import dtos.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import services.OrderService;
import models.Order;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(){
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable int id) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(id);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Order> changeUserRole(@PathVariable int id) {

        Order updatedOrder = orderService.updateOrderStatus(id);
        if(updatedOrder != null)
            return ResponseEntity.ok(updatedOrder);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update order status");
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createRegularOrder(@RequestParam List<Integer> orderProductIds, @RequestParam int userId) {
        Order order = orderService.createRegularOrder(orderProductIds, userId);
        return ResponseEntity.ok(order);
    }
    @PostMapping("/create-by-delivery-id")
    public ResponseEntity<Order> createRegularOrderWithDeliveryId(@RequestParam List<Integer> orderProductIds, @RequestParam int deliveryId) {
        Order order = orderService.createRegularOrderWithDeliveryId(orderProductIds, deliveryId);
        return ResponseEntity.ok(order);
    }
    @PostMapping("/create-express-order")
    public ResponseEntity<Order> createExpressOrder(@RequestParam List<Integer> orderProductIds, @RequestParam int deliveryId) {
        Order order = orderService.createExpressOrder(orderProductIds, deliveryId);
        return ResponseEntity.ok(order);
    }

}
