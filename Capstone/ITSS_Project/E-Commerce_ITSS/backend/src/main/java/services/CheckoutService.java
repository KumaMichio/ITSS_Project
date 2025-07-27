package services;

import lombok.RequiredArgsConstructor;
import models.Order;
import models.OrderItem;
import models.TransactionInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.OrderRepository;
import repositories.OrderItemRepository;
import repositories.TransactionInformationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final OrderServiceV2 orderServiceV2;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TransactionInformationRepository transactionInformationRepository;

    /**
     * Tạo order từ cart data - gọi trước khi chuyển đến VNPay
     */
    @Transactional
    public Order createOrderFromCart(
            List<Integer> productIds,
            List<Integer> quantities,
            String customerEmail,
            String customerFullName,
            String customerPhone,
            String deliveryAddress,
            String deliveryProvince,
            boolean isExpress) {

        System.out.println("🛒 Creating order from cart:");
        System.out.println("  - Products: " + productIds.size());
        System.out.println("  - Customer: " + customerFullName + " (" + customerEmail + ")");
        System.out.println("  - Address: " + deliveryAddress + ", " + deliveryProvince);
        System.out.println("  - Express: " + isExpress);

        // Create order with order items
        Order order = orderServiceV2.createGuestOrder(
                productIds,
                quantities,
                customerEmail,
                customerFullName,
                customerPhone,
                deliveryAddress,
                deliveryProvince,
                isExpress);

        System.out.println("✅ Order created with ID: " + order.getOrderId());
        System.out.println("  - Total amount: " + order.getTotalAmount());
        System.out.println("  - Order items count: " + order.getOrderItems().size());

        return order;
    }

    /**
     * Hoàn tất thanh toán - gọi sau khi VNPay thành công
     * Cập nhật trạng thái order và tạo transaction record
     */
    @Transactional
    public TransactionInformation completePayment(
            int orderId,
            Map<String, String> vnpayParams) {

        System.out.println("💳 Completing payment for order: " + orderId);

        String vnpTransactionNo = vnpayParams.get("vnp_TransactionNo");
        String vnpAmount = vnpayParams.get("vnp_Amount");
        String vnpBankCode = vnpayParams.get("vnp_BankCode");
        String vnpBankTranNo = vnpayParams.get("vnp_BankTranNo");
        String vnpResponseCode = vnpayParams.get("vnp_ResponseCode");
        String vnpOrderInfo = vnpayParams.getOrDefault("vnp_OrderInfo", "Thanh toan don hang " + orderId);

        // Find order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Calculate total fee from VNPay amount
        double totalFee = Double.parseDouble(vnpAmount) / 100.0;

        // Create transaction information
        TransactionInformation transaction = TransactionInformation.builder()
                .order(order)
                .totalFee(totalFee)
                .status("SUCCESS")
                .transactionTime(LocalDateTime.now())
                .content(vnpOrderInfo)
                .paymentMethod("VNPay")
                .vnpTransactionNo(vnpTransactionNo)
                .vnpBankCode(vnpBankCode)
                .vnpBankTranNo(vnpBankTranNo)
                .vnpResponseCode(vnpResponseCode)
                .orderReference(String.valueOf(orderId))
                .build();

        TransactionInformation savedTransaction = transactionInformationRepository.save(transaction);
        System.out.println("✅ Transaction saved with ID: " + savedTransaction.getTransactionId());

        // Update order status
        order.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
        order.setPayment(true);
        orderRepository.save(order);

        System.out.println("✅ Order " + orderId + " marked as paid");
        System.out.println("✅ Payment process completed successfully");

        // Clear any remaining session cart items
        clearSessionCartAfterCheckout(null); // sessionId not needed for now

        return savedTransaction;
    }

    /**
     * Kiểm tra trạng thái order
     */
    public Order getOrderStatus(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    /**
     * Clear any remaining session cart items after successful checkout
     */
    public void clearSessionCartAfterCheckout(String sessionId) {
        try {
            // Clear any orphaned OrderItems without order_id (session cart items)
            List<OrderItem> orphanedItems = orderItemRepository.findByOrderIsNull();
            if (!orphanedItems.isEmpty()) {
                orderItemRepository.deleteAll(orphanedItems);
                System.out.println("🧹 Cleared " + orphanedItems.size() + " orphaned session cart items");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to clear session cart: " + e.getMessage());
            // Non-critical, continue
        }
    }
}
