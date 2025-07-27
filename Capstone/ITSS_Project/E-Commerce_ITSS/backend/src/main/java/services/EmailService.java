package services;

import models.Order;
import models.OrderItem;
import models.Product;
import repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ProductRepository productRepository;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl; // Updated to use the correct frontend URL

    /**
     * Send order confirmation email to customer
     * Note: This is a mock implementation. In production, you would integrate with
     * an email service like SendGrid, AWS SES, or SMTP server.
     */
    public void sendOrderConfirmationEmail(Order order) {
        String emailContent = generateOrderConfirmationEmailContent(order);

        // Mock email sending - in production, replace with actual email service
        System.out.println("=== EMAIL CONFIRMATION ===");
        System.out.println("To: guest@example.com"); // Guest orders don't have email in DeliveryInformation
        System.out.println("Subject: Xác nhận đơn hàng #" + order.getOrderId());
        System.out.println("Content:");
        System.out.println(emailContent);
        System.out.println("=========================");

        // TODO: Integrate with actual email service
        // For guest orders, we would need to store email separately
        // emailProvider.sendEmail(guestEmail, subject, emailContent);
    }

    /**
     * Generate order confirmation email content
     */
    private String generateOrderConfirmationEmailContent(Order order) {
        StringBuilder content = new StringBuilder();

        content.append("Kính chào ").append(order.getCustomerFullName()).append(",\n\n");
        content.append("Cảm ơn bạn đã đặt hàng tại cửa hàng của chúng tôi!\n\n");

        content.append("THÔNG TIN ĐỚN HÀNG\n");
        content.append("==================\n");
        content.append("Mã đơn hàng: #").append(order.getOrderId()).append("\n");
        content.append("Ngày đặt: ")
                .append(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        content.append("Phương thức giao hàng: ").append(order.getShippingMethod().getMethodName()).append("\n\n");

        content.append("THÔNG TIN GIAO HÀNG\n");
        content.append("===================\n");
        content.append("Người nhận: ").append(order.getCustomerFullName()).append("\n");
        content.append("Điện thoại: ").append(order.getCustomerPhone()).append("\n");
        content.append("Địa chỉ: ").append(order.getDeliveryAddress()).append("\n");
        content.append("Tỉnh/Thành phố: ").append(order.getDeliveryProvince()).append("\n\n");

        content.append("CHI TIẾT SẢN PHẨM\n");
        content.append("=================\n");

        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                content.append("- ").append(product.getTitle()).append("\n");
                content.append("  Số lượng: ").append(item.getQuantity()).append("\n");
                content.append("  Đơn giá: ").append(String.format("%,d", (int) product.getPrice())).append(" VND\n");
                content.append("  Thành tiền: ").append(String.format("%,d", (int) item.getPrice())).append(" VND\n\n");
            }
        }

        content.append("TỔNG KẾT\n");
        content.append("========\n");
        content.append("Tổng tiền hàng: ").append(String.format("%,d", (int) order.getSubtotalAmount().doubleValue()))
                .append(" VND\n");
        content.append("Phí vận chuyển: ").append(String.format("%,d", (int) order.getShippingFees())).append(" VND\n");
        if (order.getVAT() > 0) {
            content.append("VAT: ").append(String.format("%,d", order.getVAT())).append(" VND\n");
        }
        content.append("TỔNG CỘNG: ").append(String.format("%,d", (int) order.getTotalAmount())).append(" VND\n\n");

        // Generate cancellation token
        String cancelToken = generateCancelToken(order);

        content.append("QUẢN LÝ ĐỚN HÀNG\n");
        content.append("================\n");
        content.append("Để xem chi tiết đơn hàng: ").append(frontendUrl).append("/order/").append(order.getOrderId())
                .append("\n");
        content.append("Để hủy đơn hàng (trước khi duyệt): ").append(frontendUrl).append("/order/cancel/")
                .append(cancelToken).append("\n\n");

        content.append("Lưu ý:\n");
        content.append("- Bạn có thể hủy đơn hàng miễn phí trước khi đơn hàng được duyệt\n");
        content.append(
                "- Nếu hủy đơn sau khi thanh toán, số tiền sẽ được hoàn lại qua VNPay trong 3-5 ngày làm việc\n");
        content.append("- Thời gian giao hàng: 2-5 ngày làm việc (giao hàng nhanh: 2 giờ tại nội thành Hà Nội)\n\n");

        content.append("Cảm ơn bạn đã tin tưởng và mua sắm tại cửa hàng!\n\n");
        content.append("Trân trọng,\n");
        content.append("Đội ngũ ITSS Store");

        return content.toString();
    }

    /**
     * Generate cancel token for order
     */
    private String generateCancelToken(Order order) {
        // Simple token generation - in production, use more secure method
        return UUID.randomUUID().toString() + "-" + order.getOrderId();
    }

    /**
     * Send order status update email
     */
    public void sendOrderStatusUpdateEmail(Order order, String status, String reason) {
        StringBuilder content = new StringBuilder();
        content.append("Kính chào ").append(order.getCustomerFullName()).append(",\n\n");
        content.append("Trạng thái đơn hàng #").append(order.getOrderId()).append(" đã được cập nhật.\n\n");

        content.append("Trạng thái mới: ").append(status).append("\n");
        if (reason != null && !reason.trim().isEmpty()) {
            content.append("Lý do: ").append(reason).append("\n");
        }
        content.append("Thời gian cập nhật: ")
                .append(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");

        content.append("Bạn có thể xem chi tiết đơn hàng tại: ").append(frontendUrl).append("/order/")
                .append(order.getOrderId()).append("\n\n");

        content.append("Cảm ơn bạn!\n");
        content.append("Đội ngũ ITSS Store");

        // Mock email sending
        System.out.println("=== ORDER STATUS UPDATE EMAIL ===");
        System.out.println("To: guest@example.com"); // Guest orders don't have email in DeliveryInformation
        System.out.println("Subject: Cập nhật đơn hàng #" + order.getOrderId());
        System.out.println("Content:");
        System.out.println(content.toString());
        System.out.println("================================");
    }

    /**
     * Send order confirmation email to guest customer with email
     */
    public void sendGuestOrderConfirmationEmail(Order order, String guestEmail) {
        String emailContent = generateOrderConfirmationEmailContent(order);

        // Mock email sending - in production, replace with actual email service
        System.out.println("=== GUEST EMAIL CONFIRMATION ===");
        System.out.println("To: " + guestEmail);
        System.out.println("Subject: Xác nhận đơn hàng #" + order.getOrderId());
        System.out.println("Content:");
        System.out.println(emailContent);
        System.out.println("===============================");

        // TODO: Integrate with actual email service
        // emailProvider.sendEmail(guestEmail, subject, emailContent);
    }
}
