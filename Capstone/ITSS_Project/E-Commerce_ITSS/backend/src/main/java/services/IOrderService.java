package services;

import models.Order;
import dtos.OrderDTO;

import java.util.List;

public interface IOrderService {
    Order createRegularOrder(List<Integer> orderProductIds, int userId);

    Order createRegularOrderWithDeliveryId(List<Integer> orderProductIds, int deliveryId);

    Order createExpressOrder(List<Integer> orderProductIds, int deliveryId);

    List<Order> getAllOrders();

    List<OrderDTO> getOrdersByUserId(int userId);

    Order updateOrderStatus(int id);
}
