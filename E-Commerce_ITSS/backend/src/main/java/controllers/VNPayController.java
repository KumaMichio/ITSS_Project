package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.VNPayIntegrationService;
import vnpay.dto.VNPayRequest;
import vnpay.dto.VNPayResponse;
import models.Order;
import models.TransactionInformation;
import repositories.OrderRepository;
import repositories.TransactionInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class VNPayController {

    @Autowired
    private VNPayIntegrationService vnPayService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

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
                // Transaction is successful - frontend will handle saving to DB via separate
                // API
                System.out.println("✅ VNPay callback: Payment successful, letting frontend handle DB save");
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

    /**
     * API để lưu thông tin giao dịch khi thanh toán thành công
     */
    @PostMapping("/vnpay/save-transaction")
    public ResponseEntity<Map<String, Object>> saveSuccessfulTransaction(@RequestBody Map<String, String> params) {
        try {
            System.out.println("=== SAVE TRANSACTION API CALLED ===");
            System.out.println("📥 Received request at: " + LocalDateTime.now());
            System.out.println("📥 Request parameters: " + params);
            System.out.println("📥 Parameters count: " + params.size());

            // Log each parameter individually
            params.forEach((key, value) -> {
                System.out.println("   " + key + " = " + value);
            });

            // Verify required parameters
            String vnpTxnRef = params.get("vnp_TxnRef");
            String vnpAmount = params.get("vnp_Amount");
            String vnpResponseCode = params.get("vnp_ResponseCode");

            System.out.println("🔍 Key parameters:");
            System.out.println("   vnp_TxnRef: " + vnpTxnRef);
            System.out.println("   vnp_Amount: " + vnpAmount);
            System.out.println("   vnp_ResponseCode: " + vnpResponseCode);

            if (vnpTxnRef == null || vnpAmount == null || !"00".equals(vnpResponseCode)) {
                System.err.println("❌ Invalid transaction parameters:");
                System.err.println("   vnpTxnRef is null: " + (vnpTxnRef == null));
                System.err.println("   vnpAmount is null: " + (vnpAmount == null));
                System.err.println("   vnpResponseCode is not '00': " + (!("00".equals(vnpResponseCode))));

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Invalid transaction parameters");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Save transaction information to DB
            System.out.println("💾 Starting database save process...");

            Order order = null;
            int orderIdForTransaction = 0;

            try {
                long orderIdLong = Long.parseLong(vnpTxnRef);
                System.out.println("💾 Parsed order ID as long: " + orderIdLong);

                // Check if the order ID fits in int range
                if (orderIdLong <= Integer.MAX_VALUE) {
                    orderIdForTransaction = (int) orderIdLong;
                    System.out.println("💾 Order ID fits in int range: " + orderIdForTransaction);
                    order = orderRepository.findById(orderIdForTransaction).orElse(null);
                    System.out.println("💾 Order lookup result: " + (order != null ? "Found" : "Not found"));
                } else {
                    System.out.println("💾 Order ID too large for int, storing as transaction reference only");
                    orderIdForTransaction = 0; // Set to 0 or some default value
                }
            } catch (NumberFormatException e) {
                System.out.println("💾 Could not parse order ID: " + vnpTxnRef + ", storing without order reference");
                orderIdForTransaction = 0;
            }

            // Log warning if order not found but continue to save transaction
            if (order == null) {
                System.out.println("⚠️  Warning: Order with ID " + orderIdForTransaction
                        + " not found in database, but continuing to save transaction");
            }

            // Convert VNPay amount (smallest unit) to actual amount
            double totalFee = Double.parseDouble(vnpAmount) / 100.0;
            System.out.println("💾 Converted amount: " + vnpAmount + " -> " + totalFee);

            TransactionInformation transactionInfo = TransactionInformation.builder()
                    .order(order)
                    .totalFee(totalFee)
                    .status("SUCCESS")
                    .transactionTime(LocalDateTime.now())
                    .content(params.getOrDefault("vnp_OrderInfo", "Thanh toan don hang " + vnpTxnRef))
                    .paymentMethod("VNPay")
                    .vnpTransactionNo(params.get("vnp_TransactionNo"))
                    .vnpBankCode(params.get("vnp_BankCode"))
                    .vnpBankTranNo(params.get("vnp_BankTranNo"))
                    .vnpResponseCode(params.get("vnp_ResponseCode"))
                    .orderReference(vnpTxnRef) // Store original order reference
                    .build();

            System.out.println("💾 Built transaction object: " + transactionInfo);
            System.out.println("💾 Saving to database...");

            TransactionInformation savedTransaction = transactionInfoRepository.save(transactionInfo);

            System.out.println("✅ Transaction saved successfully!");
            System.out.println("✅ Generated transaction ID: " + savedTransaction.getTransactionId());
            System.out.println("✅ Saved transaction details: " + savedTransaction);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", savedTransaction.getTransactionId());
            response.put("message", "Transaction information saved successfully");

            System.out.println("📤 Sending response: " + response);
            System.out.println("=== SAVE TRANSACTION API COMPLETED ===");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ ERROR in save transaction API:");
            System.err.println("❌ Error message: " + e.getMessage());
            System.err.println("❌ Error class: " + e.getClass().getSimpleName());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to save transaction: " + e.getMessage());
            System.err.println("📤 Sending error response: " + errorResponse);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Test endpoint để kiểm tra CORS và kết nối
     */
    @GetMapping("/vnpay/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        System.out.println("=== TEST CONNECTION API CALLED ===");
        System.out.println("📞 Test endpoint accessed at: " + LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Backend is running and CORS is working");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("server", "VNPay Controller");

        System.out.println("📤 Test response: " + response);
        return ResponseEntity.ok(response);
    }
}
