package controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public List<OrderProduct> getAllOrderProducts() {
        return cartService.getAllOrderProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProduct> getOrderProductById(@PathVariable int id) {
        Optional<OrderProduct> orderProduct = cartService.getOrderProductById(id);
        return orderProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrderProduct addOrderProduct(@RequestBody OrderProduct orderProduct) {
        return cartService.addOrderProduct(orderProduct);
    }

    @PostMapping("/add")
    public List<OrderProduct> addOrderProducts(@RequestBody List<OrderProduct> orderProducts) {
        return cartService.addOrderProducts(orderProducts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderProduct> updateOrderProduct(@PathVariable int id, @RequestBody OrderProduct orderProduct) {
        OrderProduct updatedOrderProduct = cartService.updateOrderProduct(id, orderProduct);
        if (updatedOrderProduct != null) {
            return ResponseEntity.ok(updatedOrderProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/order/{id}")
    public ResponseEntity<String> updateOrderProductId(@RequestBody List<Integer> orderProductIds, @PathVariable int id) {
        String result = cartService.updateOrderProductId(orderProductIds, id);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderProduct(@PathVariable int id) {
        cartService.deleteOrderProduct(id);
        return ResponseEntity.noContent().build();
    }
}
