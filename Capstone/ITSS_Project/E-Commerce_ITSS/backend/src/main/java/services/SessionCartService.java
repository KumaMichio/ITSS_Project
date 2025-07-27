package services;

import lombok.RequiredArgsConstructor;
import models.OrderItem;
import models.Product;
import org.springframework.stereotype.Service;
import repositories.OrderItemRepository;
import repositories.ProductRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service để quản lý session cart riêng biệt
 * Cart items sẽ được lưu trong bảng orderitem với order_id = NULL
 * Khi checkout, sẽ chuyển đổi thành order items thực sự
 */
@Service
@RequiredArgsConstructor
public class SessionCartService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    /**
     * Thêm item vào session cart (order_id = null)
     */
    public OrderItem addToSessionCart(int productId, int quantity, String sessionId) {
        // Check if item already exists in session cart
        Optional<OrderItem> existingItem = findSessionCartItem(productId, sessionId);

        if (existingItem.isPresent()) {
            // Update existing item
            OrderItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            updateProductPrice(item);
            return orderItemRepository.save(item);
        } else {
            // Create new session cart item
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            OrderItem newItem = OrderItem.orderitemBuilder()
                    .productId(productId)
                    .quantity(quantity)
                    .price(product.getPrice() * quantity)
                    .order(null) // Session cart items have no order
                    .build();

            return orderItemRepository.save(newItem);
        }
    }

    /**
     * Lấy tất cả items trong session cart
     */
    public List<OrderItem> getSessionCartItems(String sessionId) {
        // For now, return all items with order = null
        // In future, can use sessionId to filter
        return orderItemRepository.findByOrderIsNull();
    }

    /**
     * Update quantity của session cart item
     */
    public OrderItem updateSessionCartItem(int itemId, int newQuantity) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));

        if (item.getOrder() != null) {
            throw new RuntimeException("Cannot update non-session cart item");
        }

        if (newQuantity <= 0) {
            orderItemRepository.delete(item);
            return null;
        }

        item.setQuantity(newQuantity);
        updateProductPrice(item);
        return orderItemRepository.save(item);
    }

    /**
     * Xóa item khỏi session cart
     */
    public void removeFromSessionCart(int itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + itemId));

        if (item.getOrder() != null) {
            throw new RuntimeException("Cannot remove non-session cart item");
        }

        orderItemRepository.delete(item);
    }

    /**
     * Xóa toàn bộ session cart
     */
    public void clearSessionCart(String sessionId) {
        List<OrderItem> sessionItems = getSessionCartItems(sessionId);
        orderItemRepository.deleteAll(sessionItems);
    }

    /**
     * Chuyển session cart thành order items thực sự
     */
    public void convertSessionCartToOrder(String sessionId, models.Order order) {
        List<OrderItem> sessionItems = getSessionCartItems(sessionId);

        for (OrderItem item : sessionItems) {
            item.setOrder(order);
            orderItemRepository.save(item);
        }
    }

    // Helper methods
    private Optional<OrderItem> findSessionCartItem(int productId, String sessionId) {
        // For now, find by productId and order = null
        // In future, can add sessionId field to OrderItem
        return orderItemRepository.findByProductIdAndOrderIsNull(productId);
    }

    private void updateProductPrice(OrderItem item) {
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

        item.setPrice(product.getPrice() * item.getQuantity());
    }
}
