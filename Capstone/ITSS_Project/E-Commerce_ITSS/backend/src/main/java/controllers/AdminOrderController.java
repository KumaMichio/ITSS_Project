package controllers;

import lombok.RequiredArgsConstructor;
import models.Order;
import models.OrderItem;
import models.TransactionInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repositories.OrderRepository;
import repositories.OrderItemRepository;
import services.PaymentManagementService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentManagementService paymentManagementService;

    /**
     * Get all orders with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Get all orders sorted by creation date (newest first)
            List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("content", orders);
            response.put("totalItems", orders.size());
            response.put("currentPage", page);
            response.put("totalPages", (orders.size() + size - 1) / size);
            response.put("pageSize", size);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get order details by ID including order items and transaction
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable int orderId) {
        try {
            System.out.println("🔍 AdminOrderController: Getting order details for ID: " + orderId);

            // Get order
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy đơn hàng với ID: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Order order = orderOpt.get();
            System.out.println("✅ Found order: " + order.getOrderId());

            // Get order items with products (using JOIN FETCH to avoid lazy loading issues)
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithProduct(orderId);
            System.out.println("✅ Found " + orderItems.size() + " order items");

            // Get transaction information
            Optional<TransactionInformation> transaction = paymentManagementService.getPaymentByOrderId(orderId);
            System.out.println("✅ Transaction found: " + transaction.isPresent());

            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("order", order);
            orderDetails.put("orderItems", orderItems);
            orderDetails.put("transaction", transaction.orElse(null));
            orderDetails.put("hasTransaction", transaction.isPresent());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", orderDetails);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error getting order details: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get order statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        try {
            List<Order> allOrders = orderRepository.findAllByOrderByCreatedAtDesc();

            long totalOrders = allOrders.size();
            long paidOrders = allOrders.stream().mapToLong(order -> order.isPayment() ? 1 : 0).sum();
            long unpaidOrders = totalOrders - paidOrders;

            double totalRevenue = allOrders.stream()
                    .filter(Order::isPayment)
                    .mapToDouble(Order::getTotalAmount)
                    .sum();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalOrders", totalOrders);
            statistics.put("paidOrders", paidOrders);
            statistics.put("unpaidOrders", unpaidOrders);
            statistics.put("totalRevenue", totalRevenue);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy thống kê đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
