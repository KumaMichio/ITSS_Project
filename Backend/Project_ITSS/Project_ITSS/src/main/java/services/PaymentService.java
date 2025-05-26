package services;

import config.VNPayConfig;
import dtos.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import models.Order;
import org.springframework.stereotype.Service;
import repositories.OrderRepository;

import java.util.Map;
import java.util.Optional;
import util.VNPayUtil;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService{
    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepository;

    @Override
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(0);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Override
    public PaymentDTO.VNPayResponse createVnPayPaymentForOrder(HttpServletRequest request, int orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            long amount = (long) (order.getTotalFees() * 100L); // Assuming order amount is in smallest currency unit (e.g., cents)
            String bankCode = request.getParameter("bankCode");
            Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderId);
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

            return PaymentDTO.VNPayResponse.builder()
                    .code("ok")
                    .message("success")
                    .paymentUrl(paymentUrl).build();
        } else {
            // Handle the case where order is not found
            return PaymentDTO.VNPayResponse.builder()
                    .code("error")
                    .message("Order not found")
                    .paymentUrl("").build();
        }
    }
}
