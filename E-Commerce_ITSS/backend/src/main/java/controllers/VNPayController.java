package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.VNPayIntegrationService;
import vnpay.dto.VNPayRequest;
import vnpay.dto.VNPayResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class VNPayController {

    @Autowired
    private VNPayIntegrationService vnPayService;

    /**
     * Tạo URL thanh toán VNPay từ request body
     */
    @PostMapping("/vnpay/create-order")
    public ResponseEntity<Map<String, Object>> createVNPayPayment(
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {

        try {
            // Extract order information
            Long orderId = requestData.get("orderId") != null ? Long.valueOf(requestData.get("orderId").toString())
                    : System.currentTimeMillis();
            Long amount = requestData.get("amount") != null ? Long.valueOf(requestData.get("amount").toString())
                    : 100000L;
            String orderInfo = requestData.get("orderInfo") != null ? requestData.get("orderInfo").toString()
                    : "Payment for order " + orderId;

            // Debug logging
            System.out.println("=== VNPay Payment Debug ===");
            System.out.println("Request Data: " + requestData);
            System.out.println("OrderId: " + orderId);
            System.out.println("Amount from frontend: " + amount);
            System.out.println("Amount type: " + amount.getClass().getSimpleName());
            System.out.println("OrderInfo: " + orderInfo);
            System.out.println("========================");

            // Create VNPay payment URL
            String paymentUrl = vnPayService.createVNPayPaymentUrl(orderId, amount, orderInfo, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "paymentUrl", paymentUrl,
                    "orderId", orderId,
                    "amount", amount));
            response.put("message", "VNPay payment URL created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error creating VNPay payment: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to create VNPay payment URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Tạo URL thanh toán VNPay mặc định
     */
    @GetMapping("/vnpay")
    public ResponseEntity<Map<String, Object>> createDefaultVNPayPayment(HttpServletRequest request) {
        try {
            Long orderId = System.currentTimeMillis();
            Long amount = 100000L; // Default amount: 100,000 VND
            String orderInfo = "Demo payment order " + orderId;

            String paymentUrl = vnPayService.createVNPayPaymentUrl(orderId, amount, orderInfo, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "paymentUrl", paymentUrl,
                    "orderId", orderId,
                    "amount", amount));
            response.put("message", "Default VNPay payment URL created");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to create default VNPay payment URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Tạo URL thanh toán cho order cụ thể
     */
    @GetMapping("/vnpay/order/{orderId}")
    public ResponseEntity<Map<String, Object>> createVNPayPaymentForOrder(
            @PathVariable Long orderId,
            HttpServletRequest request) {

        try {
            Long amount = 100000L; // Default amount, should be fetched from order
            String orderInfo = "Thanh toan don hang " + orderId;

            String paymentUrl = vnPayService.createVNPayPaymentUrl(orderId, amount, orderInfo, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "paymentUrl", paymentUrl,
                    "orderId", orderId,
                    "amount", amount));
            response.put("message", "VNPay payment URL created for order " + orderId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to create VNPay payment URL for order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Xử lý callback từ VNPay
     */
    @GetMapping("/vnpay/callback")
    public ResponseEntity<Map<String, Object>> handleVNPayCallback(@RequestParam Map<String, String> params) {
        try {
            System.out.println("Callback Parameters: " + params);

            // Verify VNPay signature
            boolean isValidSignature = vnPayService.validateResponse(params);

            System.out.println("Signature Valid: " + isValidSignature);

            if (!isValidSignature) {
                System.err.println("Signature mismatch detected. Please verify the secret key and parameters.");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", isValidSignature);
            response.put("transactionStatus", params.get("vnp_TransactionStatus"));
            response.put("responseCode", params.get("vnp_ResponseCode"));
            response.put("orderId", params.get("vnp_TxnRef"));
            response.put("amount", params.get("vnp_Amount"));
            response.put("bankCode", params.get("vnp_BankCode"));
            response.put("transactionNo", params.get("vnp_TransactionNo"));
            response.put("payDate", params.get("vnp_PayDate"));

            if (isValidSignature && "00".equals(params.get("vnp_ResponseCode"))) {
                response.put("message", "Payment successful");
            } else {
                response.put("message", "Payment failed or invalid signature");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error processing VNPay callback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * API tương thích với mẫu JSP - Tạo thanh toán từ form data
     */
    @PostMapping("/vnpayajax")
    public ResponseEntity<VNPayResponse> createPaymentAjax(
            @RequestParam("amount") Long amount,
            @RequestParam(value = "bankCode", required = false) String bankCode,
            @RequestParam(value = "language", defaultValue = "vn") String language,
            HttpServletRequest request) {

        VNPayRequest vnPayRequest = new VNPayRequest();
        vnPayRequest.setAmount(amount);
        vnPayRequest.setBankCode(bankCode);
        vnPayRequest.setLanguage(language);
        vnPayRequest.setOrderInfo("Thanh toan don hang");
        vnPayRequest.setOrderType("other");

        VNPayResponse response = vnPayService.createPaymentUrl(vnPayRequest, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Xử lý IPN (Instant Payment Notification) từ VNPay
     */
    @PostMapping("/vnpay/ipn")
    public ResponseEntity<Map<String, String>> paymentIPN(@RequestParam Map<String, String> params) {
        boolean isValid = vnPayService.validateResponse(params);

        if (isValid) {
            String vnp_ResponseCode = params.get("vnp_ResponseCode");

            if ("00".equals(vnp_ResponseCode)) {
                // Payment successful - Update order status
                return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Confirm Success"));
            } else {
                // Payment failed
                return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Payment Failed"));
            }
        } else {
            return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Invalid Signature"));
        }
    }
}
