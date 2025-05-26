package services;

import dtos.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface IPaymentService {
    PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request);
    PaymentDTO.VNPayResponse createVnPayPaymentForOrder(HttpServletRequest request, int orderId);
}
