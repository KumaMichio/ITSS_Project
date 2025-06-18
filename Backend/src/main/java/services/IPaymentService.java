package services;

import dtos.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import models.Order;

public interface IPaymentService {
    PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request);

    PaymentDTO.VNPayResponse createVnPayPaymentForOrder(HttpServletRequest request, int orderId);

    // New method to match VNPAY-Payment-Integrate structure
    java.util.Map<String, Object> createOrder(HttpServletRequest request, PaymentDTO.VNPayRequest orderRequest)
            throws java.io.UnsupportedEncodingException;

    // Order management methods
    Order getOrderById(int orderId);

    boolean updateOrderPaymentStatus(int orderId, String status);

    // Refund methods
    boolean processRefund(int orderId, String reason);

    String createVnPayPayment(int orderId, int amount, String orderInfo, String baseUrl);
}
