package controllers;

import dtos.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseObject;
import services.PaymentService;
import services.TransactionService;

import java.io.IOException;
import java.net.URLDecoder;

@RestController
@RequestMapping("${spring.application.api-prefix}/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final TransactionService transactionService;


    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }

    @GetMapping("/vn-pay-order/{orderId}")
    public ResponseObject<PaymentDTO.VNPayResponse> payForOrder(HttpServletRequest request, @PathVariable int orderId) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPaymentForOrder(request, orderId));
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("vnp_ResponseCode");
        int id = Integer.parseInt(URLDecoder.decode(request.getParameter("vnp_OrderInfo"), "UTF-8").split(":")[1]);
        if ("00".equals(status)) {
            transactionService.createTransaction(id, "Thanh Cong");
            // Điều hướng đến trang web frontend sau khi thanh toán thành công
            response.sendRedirect("http://localhost:3000/transaction/success?orderId="+id);  // Đổi thành URL của trang web frontend sau khi thanh toán thành công
        } else {
            transactionService.createTransaction(id, "That Bai");
            // Xử lý điều hướng khi thanh toán thất bại
            response.sendRedirect("http://localhost:3000/transaction/fail"); ; // Đổi thành URL của trang web frontend khi thanh toán thất bại
        }
    }
}
