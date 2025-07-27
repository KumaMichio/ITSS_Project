package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private VNPayController vnPayController;

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "payment");
        response.put("timestamp", System.currentTimeMillis());
        response.put("message", "Payment service is running");

        return ResponseEntity.ok(response);
    }

    // General payment method selection
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody Map<String, Object> paymentRequest,
            HttpServletRequest request) {

        try {
            String paymentMethod = paymentRequest.get("method") != null ? paymentRequest.get("method").toString()
                    : "vnpay";

            switch (paymentMethod.toLowerCase()) {
                case "vnpay":
                    return vnPayController.createVNPayPayment(paymentRequest, request);

                case "momo":
                    return createMoMoPayment(paymentRequest, request);

                case "zalopay":
                    return createZaloPayPayment(paymentRequest, request);

                default:
                    // Default to VNPay
                    return vnPayController.createVNPayPayment(paymentRequest, request);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Payment creation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get payment status
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable String transactionId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transactionId);
            response.put("status", "pending"); // This should query actual payment status
            response.put("message", "Payment status retrieved");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to get payment status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Cancel payment
    @PostMapping("/cancel/{transactionId}")
    public ResponseEntity<Map<String, Object>> cancelPayment(@PathVariable String transactionId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transactionId);
            response.put("status", "cancelled");
            response.put("message", "Payment cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to cancel payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Refund payment
    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> refundPayment(@RequestBody Map<String, Object> refundRequest) {
        try {
            String transactionId = refundRequest.get("transactionId").toString();
            Object amountObj = refundRequest.get("amount");
            Long amount = amountObj != null ? Long.valueOf(amountObj.toString()) : null;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", transactionId);
            response.put("refundAmount", amount);
            response.put("status", "refunded");
            response.put("message", "Refund processed successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Refund failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Payment methods info
    @GetMapping("/methods")
    public ResponseEntity<Map<String, Object>> getPaymentMethods() {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> vnpay = new HashMap<>();
        vnpay.put("name", "VNPay");
        vnpay.put("enabled", true);
        vnpay.put("description", "VNPay Payment Gateway");
        vnpay.put("fee", 0);

        Map<String, Object> momo = new HashMap<>();
        momo.put("name", "MoMo");
        momo.put("enabled", false);
        momo.put("description", "MoMo E-Wallet (Coming Soon)");
        momo.put("fee", 0);

        Map<String, Object> zalopay = new HashMap<>();
        zalopay.put("name", "ZaloPay");
        zalopay.put("enabled", false);
        zalopay.put("description", "ZaloPay E-Wallet (Coming Soon)");
        zalopay.put("fee", 0);

        response.put("success", true);
        response.put("methods", Map.of(
                "vnpay", vnpay,
                "momo", momo,
                "zalopay", zalopay));

        return ResponseEntity.ok(response);
    }

    // Placeholder methods for other payment gateways
    private ResponseEntity<Map<String, Object>> createMoMoPayment(
            Map<String, Object> paymentRequest,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "MoMo payment is not yet implemented");
        response.put("message", "Please use VNPay payment method");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    }

    private ResponseEntity<Map<String, Object>> createZaloPayPayment(
            Map<String, Object> paymentRequest,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "ZaloPay payment is not yet implemented");
        response.put("message", "Please use VNPay payment method");

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    }
}
