package services;

import config.VNPayConfig;
import dtos.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import models.Order;
import org.springframework.stereotype.Service;
import repositories.OrderRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import util.VNPayUtil;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {
    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final VNPayOrderService vnPayOrderService;

    @Override
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        try {
            String amountParam = request.getParameter("amount");
            if (amountParam == null || amountParam.isEmpty()) {
                // Default amount for testing
                amountParam = "100000";
            }

            long amount = Long.parseLong(amountParam) * 100L;
            String bankCode = request.getParameter("bankCode");

            Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(0);
            vnpParamsMap.put("vnp_Amount", String.valueOf(amount));

            if (bankCode != null && !bankCode.isEmpty()) {
                vnpParamsMap.put("vnp_BankCode", bankCode);
            }

            vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

            // Build query URL
            String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
            String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
            String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

            String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

            System.out.println("Generated VNPay URL: " + paymentUrl);

            return PaymentDTO.VNPayResponse.builder()
                    .code("ok")
                    .message("success")
                    .paymentUrl(paymentUrl).build();

        } catch (NumberFormatException e) {
            System.err.println("Invalid amount format: " + e.getMessage());
            return PaymentDTO.VNPayResponse.builder()
                    .code("error")
                    .message("Invalid amount format")
                    .paymentUrl("").build();
        } catch (Exception e) {
            System.err.println("Error creating VNPay payment: " + e.getMessage());
            e.printStackTrace();
            return PaymentDTO.VNPayResponse.builder()
                    .code("error")
                    .message("Error creating payment: " + e.getMessage())
                    .paymentUrl("").build();
        }
    }

    @Override
    public PaymentDTO.VNPayResponse createVnPayPaymentForOrder(HttpServletRequest request, int orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();
        long amount = Math.max((long) (order.getTotalFees() * 100L), 100000L);
        String orderInfo = "Thanh toan don hang " + orderId;

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderId);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        System.out.println("Generated VNPay URL for order " + orderId + ": " + paymentUrl);

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public Map<String, Object> createOrder(HttpServletRequest request, PaymentDTO.VNPayRequest orderRequest)
            throws java.io.UnsupportedEncodingException {
        try {
            // Use the new VNPayOrderService that matches the standalone implementation
            return vnPayOrderService.createVNPayOrder(request, orderRequest);
        } catch (Exception e) {
            System.err.println("Error creating VNPay order: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create VNPay order", e);
        }
    }

    @Override
    public boolean updateOrderPaymentStatus(int orderId, String status) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // You might want to add a payment status field to Order entity
                // For now, we'll just log the status update
                System.out.println("Updated payment status for order " + orderId + " to: " + status);
                System.out.println("Order details: " + order.toString());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean processRefund(int orderId, String reason) {
        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (!orderOpt.isPresent()) {
                System.err.println("Order not found for refund: " + orderId);
                return false;
            }

            Order order = orderOpt.get();

            // For now, this is a mock implementation
            // In a real implementation, you would call VNPay refund API using:
            // POST to: https://sandbox.vnpayment.vn/merchant_webapi/api/transaction

            System.out.println("=== VNPay Refund Request ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("Amount: " + order.getTotalFees() + " VND");
            System.out.println("Reason: " + (reason != null ? reason : "No reason provided"));
            System.out.println("API URL: " + vnPayConfig.getApiUrl());
            System.out.println("TMN Code: " + vnPayConfig.getTmnCode());

            // TODO: Implement actual VNPay refund API call
            // Map<String, String> refundParams = new HashMap<>();
            // refundParams.put("vnp_RequestId", VNPayUtil.getRandomNumber(8));
            // refundParams.put("vnp_Version", vnPayConfig.getVersion());
            // refundParams.put("vnp_Command", "refund");
            // refundParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            // refundParams.put("vnp_TransactionType", "02");
            // refundParams.put("vnp_TxnRef", "ORDER" + orderId);
            // refundParams.put("vnp_Amount", String.valueOf((long)(order.getTotalFees() *
            // 100)));
            // refundParams.put("vnp_OrderInfo", "Hoan tien don hang: " + orderId);
            // refundParams.put("vnp_TransactionNo", ""); // Get from original payment
            // refundParams.put("vnp_TransactionDate", ""); // Get from original payment
            // refundParams.put("vnp_CreateBy", "system");
            // refundParams.put("vnp_CreateDate", new
            // SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            // refundParams.put("vnp_IpAddr", "127.0.0.1");

            // String hashData = VNPayUtil.getPaymentURL(refundParams, false);
            // String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(),
            // hashData);
            // refundParams.put("vnp_SecureHash", vnpSecureHash);

            // Call VNPay refund API and process response

            System.out.println("Refund processing completed (mock)");
            return true;

        } catch (Exception e) {
            System.err.println("Error processing refund for order " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String createVnPayPayment(int orderId, int amount, String orderInfo, String baseUrl) {
        try {
            Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderId);
            vnpParamsMap.put("vnp_Amount", String.valueOf(amount * 100L)); // Convert to smallest currency unit
            vnpParamsMap.put("vnp_OrderInfo", orderInfo);
            vnpParamsMap.put("vnp_ReturnUrl", baseUrl + "/api/payment/vn-pay-callback");
            vnpParamsMap.put("vnp_IpAddr", "127.0.0.1"); // Default IP for server-side calls

            // Build query URL
            String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
            String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
            String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
            return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        } catch (Exception e) {
            System.err.println("Error creating VNPay payment URL: " + e.getMessage());
            return null;
        }
    }
}
