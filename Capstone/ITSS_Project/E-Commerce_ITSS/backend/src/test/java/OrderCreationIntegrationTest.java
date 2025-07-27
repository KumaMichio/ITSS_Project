import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import services.OrderService;
import repositories.OrderRepository;
import repositories.OrderItemRepository;
import models.Order;
import models.OrderItem;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringJUnitConfig
@Transactional
public class OrderCreationIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void testOrderCreationWithOrderItems() {
        // Giả sử có orderItems đã tồn tại với IDs: 1, 2, 3
        List<Integer> orderProductIds = Arrays.asList(1, 2, 3);
        int userId = 1; // Giả sử user có ID 1

        try {
            // Tạo order
            Order createdOrder = orderService.createRegularOrder(orderProductIds, userId);

            // Verify order được tạo thành công
            assertNotNull(createdOrder);
            assertNotNull(createdOrder.getOrderId());
            assertTrue(createdOrder.getOrderId() > 0);

            // Verify tất cả OrderItems đều có order_id được set
            List<OrderItem> orderItems = orderItemRepository.findAll()
                    .stream()
                    .filter(item -> item.getOrder() != null &&
                            item.getOrder().getOrderId() == createdOrder.getOrderId())
                    .toList();

            // Kiểm tra số lượng OrderItems
            assertEquals(orderProductIds.size(), orderItems.size());

            // Kiểm tra mỗi OrderItem đều có order_id đúng
            for (OrderItem item : orderItems) {
                assertNotNull(item.getOrder());
                assertEquals(createdOrder.getOrderId(), item.getOrder().getOrderId());
            }

            System.out.println("✅ Test PASSED: Order created successfully with OrderId: " + createdOrder.getOrderId());
            System.out.println("✅ All OrderItems have correct order_id set");

        } catch (Exception e) {
            System.err.println("❌ Test FAILED: " + e.getMessage());
            e.printStackTrace();
            fail("Order creation failed: " + e.getMessage());
        }
    }

    @Test
    public void testOrderItemsHaveNoNullOrderId() {
        // Kiểm tra không có OrderItem nào có order_id NULL
        List<OrderItem> orphanedItems = orderItemRepository.findAll()
                .stream()
                .filter(item -> item.getOrder() == null)
                .toList();

        if (!orphanedItems.isEmpty()) {
            System.err.println("❌ Found " + orphanedItems.size() + " OrderItems with NULL order_id:");
            for (OrderItem item : orphanedItems) {
                System.err.println("  - OrderItem ID: " + item.getId() +
                        ", Product ID: " + item.getProductId() +
                        ", Quantity: " + item.getQuantity());
            }
        } else {
            System.out.println("No OrderItems with NULL order_id found");
        }

        assertTrue(orphanedItems.isEmpty(),
                "Found OrderItems with NULL order_id: " + orphanedItems.size());
    }
}
