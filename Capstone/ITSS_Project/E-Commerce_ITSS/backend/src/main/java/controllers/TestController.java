package controllers;

import lombok.RequiredArgsConstructor;
import models.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import repositories.*;
import services.OrderServiceV2;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestController {

    private final OrderServiceV2 orderServiceV2;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TransactionInformationRepository transactionInformationRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final ProductRepository productRepository;

    /**
     * Test toàn bộ luồng tạo order, order items và transaction
     */
    @PostMapping("/full-payment-flow")
    @Transactional
    public ResponseEntity<Map<String, Object>> testFullPaymentFlow() {
        try {
            System.out.println("🧪 Testing full payment flow without triggers...");

            // 1. Tạo shipping method nếu chưa có
            ShippingMethod shippingMethod = shippingMethodRepository.findById(1)
                    .orElseGet(() -> {
                        ShippingMethod method = ShippingMethod.shippingMethodBuilder()
                                .methodName("Standard")
                                .isRush(false)
                                .shippingFees(30000.0)
                                .build();
                        return shippingMethodRepository.save(method);
                    });
            System.out.println("✅ Shipping method ready: " + shippingMethod.getMethodName());

            // 2. Lấy product đầu tiên để test
            List<Product> products = productRepository.findAll();
            if (products.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "No products found in database");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Product testProduct = products.get(0);
            System.out.println("✅ Using test product: " + testProduct.getTitle());

            // 3. Tạo order trực tiếp qua service
            List<Integer> productIds = List.of(testProduct.getId());
            List<Integer> quantities = List.of(1);

            Order order = orderServiceV2.createGuestOrder(
                    productIds,
                    quantities,
                    "test@example.com",
                    "Test Customer",
                    "0123456789",
                    "123 Test Street",
                    "TP.HCM",
                    false);
            System.out.println("✅ Order created: " + order.getOrderId());

            // 4. Kiểm tra order items được tạo
            List<OrderItem> orderItems = order.getOrderItems();
            System.out.println("✅ Order items created: " + orderItems.size());

            // 5. Tạo transaction info trực tiếp
            TransactionInformation transaction = TransactionInformation.builder()
                    .order(order)
                    .totalFee(order.getTotalAmount())
                    .status("SUCCESS")
                    .transactionTime(LocalDateTime.now())
                    .content("Test payment")
                    .paymentMethod("VNPAY_TEST")
                    .vnpTransactionNo("TEST_" + System.currentTimeMillis())
                    .vnpResponseCode("00")
                    .orderReference("ORDER_" + order.getOrderId())
                    .build();

            TransactionInformation savedTransaction = transactionInformationRepository.save(transaction);
            System.out.println("✅ Transaction created: " + savedTransaction.getTransactionId());

            // 6. Cập nhật order với transaction info
            order.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
            order.setPayment(true);
            Order updatedOrder = orderRepository.save(order);
            System.out.println("✅ Order updated with payment status");

            // 7. Trả về kết quả
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Full payment flow completed successfully");
            response.put("data", Map.of(
                    "orderId", updatedOrder.getOrderId(),
                    "transactionId", savedTransaction.getTransactionId(),
                    "orderItemsCount", orderItems.size(),
                    "totalAmount", updatedOrder.getTotalAmount(),
                    "isPayment", updatedOrder.isPayment(),
                    "shippingMethodId", shippingMethod.getMethodID(),
                    "productTested", testProduct.getTitle()));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Test failed: " + e.getMessage());
            errorResponse.put("errorType", e.getClass().getSimpleName());

            if (e.getCause() != null) {
                errorResponse.put("rootCause", e.getCause().getMessage());
            }

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Test database connection và các bảng chính
     */
    @GetMapping("/database-health")
    public ResponseEntity<Map<String, Object>> testDatabaseHealth() {
        try {
            Map<String, Object> healthCheck = new HashMap<>();

            // Test các bảng chính
            long productCount = productRepository.count();
            long orderCount = orderRepository.count();
            long orderItemCount = orderItemRepository.count();
            long transactionCount = transactionInformationRepository.count();
            long shippingMethodCount = shippingMethodRepository.count();

            healthCheck.put("products", productCount);
            healthCheck.put("orders", orderCount);
            healthCheck.put("orderItems", orderItemCount);
            healthCheck.put("transactions", transactionCount);
            healthCheck.put("shippingMethods", shippingMethodCount);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Database health check completed");
            response.put("data", healthCheck);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Database health check failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Cleanup test data
     */
    @DeleteMapping("/cleanup")
    @Transactional
    public ResponseEntity<Map<String, Object>> cleanupTestData() {
        try {
            // Xóa các transaction test
            List<TransactionInformation> testTransactions = transactionInformationRepository
                    .findAll()
                    .stream()
                    .filter(t -> t.getPaymentMethod() != null && t.getPaymentMethod().equals("VNPAY_TEST"))
                    .toList();

            for (TransactionInformation transaction : testTransactions) {
                transactionInformationRepository.delete(transaction);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test data cleaned up");
            response.put("deletedTransactions", testTransactions.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Cleanup failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
