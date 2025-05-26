package controllers;

import models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.CartService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public List<OrderItem> getAllOrderItems() {
        return cartService.getAllOrderItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable int id) {
        Optional<OrderItem> orderProduct = cartService.getOrderItemById(id);
        return orderProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrderItem addOrderItem(@RequestBody OrderItem orderProduct) {
        return cartService.addOrderItem(orderProduct);
    }

    @PostMapping("/add")
    public List<OrderItem> addOrderItems(@RequestBody List<OrderItem> orderProducts) {
        return cartService.addOrderItems(orderProducts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable int id, @RequestBody OrderItem orderProduct) {
        OrderItem updatedOrderItem = cartService.updateOrderItem(id, orderProduct);
        if (updatedOrderItem != null) {
            return ResponseEntity.ok(updatedOrderItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/order/{id}")
    public ResponseEntity<String> updateOrderItemId(@RequestBody List<Integer> orderProductIds, @PathVariable int id) {
        String result = cartService.updateOrderItemId(orderProductIds, id);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderProduct(@PathVariable int id) {
        cartService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
