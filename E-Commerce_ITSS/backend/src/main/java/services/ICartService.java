package services;

import models.OrderItem;

import java.util.List;
import java.util.Optional;

public interface ICartService {
    List<OrderItem> getAllOrderItems();

    Optional<OrderItem> getOrderItemById(int id);

    OrderItem addOrderItem(OrderItem orderItem);

    List<OrderItem> addOrderItems(List<OrderItem> orderItems);

    OrderItem updateOrderItem(int id, OrderItem orderItem);

    String updateOrderItemId(List<Integer> orderItemIds, int id);

    void deleteOrderItem(int id);

    void clearCart();
}
