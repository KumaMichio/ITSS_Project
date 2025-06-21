package controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class VNPayController { // VNPay Configuration - Sandbox Environment
    private static final String VNP_TMN_CODE = "ZAVGV1VT";
    private static final String VNP_SECRET_KEY = "OR92SDL9CRPL5TOXFICMKRVASZ4FXJ4M";
    private static final String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // URL user return
                                                                                                    // after payment -
                                                                                                    // match frontend
                                                                                                    // origin
    private static final String VNP_RETURN_URL = "http://localhost:5174/transaction/success";
    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_CURRENCY_CODE = "VND";
    private static final String VNP_LOCALE = "vn";

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

            // Create VNPay payment URL
            String paymentUrl = createVNPayPaymentUrl(orderId, amount, orderInfo, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "paymentUrl", paymentUrl,
                    "orderId", orderId,
                    "amount", amount));
            response.put("message", "VNPay payment URL created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to create VNPay payment URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/vnpay")
    public ResponseEntity<Map<String, Object>> createDefaultVNPayPayment(HttpServletRequest request) {
        try {
            Long orderId = System.currentTimeMillis();
            Long amount = 100000L; // Default amount: 100,000 VND
            String orderInfo = "Demo payment order " + orderId;

            String paymentUrl = createVNPayPaymentUrl(orderId, amount, orderInfo, request);

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

    @GetMapping("/vnpay/order/{orderId}")
    public ResponseEntity<Map<String, Object>> createVNPayPaymentForOrder(
            @PathVariable Long orderId,
            HttpServletRequest request) {

        try {
            Long amount = 100000L; // Default amount, should be fetched from order
            String orderInfo = "Thanh toan don hang " + orderId;

            String paymentUrl = createVNPayPaymentUrl(orderId, amount, orderInfo, request);

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

    @GetMapping("/vnpay/callback")
    public ResponseEntity<Map<String, Object>> handleVNPayCallback(@RequestParam Map<String, String> params) {
        try {
            // Verify VNPay signature
            String vnpSecureHash = params.get("vnp_SecureHash");
            params.remove("vnp_SecureHash");

            String calculatedHash = generateVNPayHash(params);
            boolean isValidSignature = vnpSecureHash != null && vnpSecureHash.equals(calculatedHash);

            Map<String, Object> response = new HashMap<>();
            response.put("success", isValidSignature);
            response.put("data", params);
            response.put("message", isValidSignature ? "Payment verification successful" : "Invalid payment signature");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Payment callback error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private String createVNPayPaymentUrl(Long orderId, Long amount, String orderInfo, HttpServletRequest request)
            throws UnsupportedEncodingException {

        // Generate transaction reference
        String txnRef = "ORDER" + orderId;

        // Generate timestamps in Vietnam timezone (UTC+7)
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(calendar.getTime());

        // Expire date (15 minutes later)
        calendar.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(calendar.getTime());

        // Get client IP address
        String ipAddr = getClientIpAddress(request); // Build parameters
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", VNP_VERSION);
        vnpParams.put("vnp_Command", VNP_COMMAND);
        vnpParams.put("vnp_TmnCode", VNP_TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // Convert to smallest unit
        vnpParams.put("vnp_CurrCode", VNP_CURRENCY_CODE);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", VNP_LOCALE);
        vnpParams.put("vnp_ReturnUrl", VNP_RETURN_URL);
        vnpParams.put("vnp_IpAddr", ipAddr);
        vnpParams.put("vnp_CreateDate", createDate);
        vnpParams.put("vnp_ExpireDate", expireDate);

        System.out.println("VNPay Parameters: " + vnpParams);
        System.out.println("Order ID: " + orderId + ", Amount: " + amount + ", TxnRef: " + txnRef); // Generate hash
                                                                                                    // (IMPORTANT:
                                                                                                    // vnp_SecureHashType
                                                                                                    // is NOT included
                                                                                                    // in hash
                                                                                                    // calculation)
        String vnpSecureHash = generateVNPayHash(vnpParams);

        // Add SecureHashType to params AFTER hash calculation for URL building
        vnpParams.put("vnp_SecureHashType", "HMACSHA512");

        // Build query string WITH URL ENCODING (only for final URL, not for hash)
        StringBuilder queryBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append("&");
            }
            queryBuilder.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
        }

        // Add secure hash (no encoding needed for hash itself)
        queryBuilder.append("&vnp_SecureHash=").append(vnpSecureHash);

        String finalUrl = VNP_PAY_URL + "?" + queryBuilder.toString();
        System.out.println("Final VNPay URL: " + finalUrl.substring(0, Math.min(finalUrl.length(), 150)) + "...");

        return finalUrl;
    }

    private String generateVNPayHash(Map<String, String> params) {
        try {
            // Sort parameters by key
            List<String> sortedKeys = new ArrayList<>(params.keySet());
            Collections.sort(sortedKeys);

            System.out.println("VNPay Hash Generation - Sorted Keys: " + sortedKeys);

            // Build hash data string
            StringBuilder hashData = new StringBuilder();
            for (String key : sortedKeys) {
                String value = params.get(key);
                if (value != null && !value.isEmpty()) {
                    if (hashData.length() > 0) {
                        hashData.append("&");
                    }
                    hashData.append(key).append("=").append(value);
                }
            }

            System.out.println("VNPay Hash Data String: " + hashData.toString());
            System.out.println("VNPay Secret Key: " + VNP_SECRET_KEY);

            // Generate HMAC-SHA512 hash
            String hash = hmacSHA512(VNP_SECRET_KEY, hashData.toString());
            System.out.println("Generated VNPay Hash: " + hash);

            return hash;

        } catch (Exception e) {
            System.err.println("Error generating VNPay hash: " + e.getMessage());
            throw new RuntimeException("Error generating VNPay hash", e);
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKeySpec);

            byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder result = new StringBuilder();
            for (byte b : hashBytes) {
                result.append(String.format("%02x", b));
            }

            return result.toString().toUpperCase();

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating HMAC-SHA512", e);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // If multiple IPs, get the first one
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        // Convert IPv6 localhost to IPv4
        if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }

        return ipAddress != null ? ipAddress : "127.0.0.1";
    }
}
