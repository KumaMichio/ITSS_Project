package repositories;

import models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // Find session cart items (items without order)
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order IS NULL")
    List<OrderItem> findByOrderIsNull();

    // Find specific session cart item by product ID
    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId = :productId AND oi.order IS NULL")
    Optional<OrderItem> findByProductIdAndOrderIsNull(@Param("productId") int productId);

    // Find order items by order ID
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") int orderId);

    // Find order items by order ID with product details
    @Query("SELECT oi FROM OrderItem oi LEFT JOIN FETCH oi.product WHERE oi.order.orderId = :orderId")
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") int orderId);
}
