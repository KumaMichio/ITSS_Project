package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Test case để so sánh với VNPay documentation
 * Dựa trên example từ VNPay API Documentation
 */
public class VNPayStandardTest {

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException("Key or Data is null");
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString().toUpperCase();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate HMAC SHA-512 signature", ex);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== VNPay Standard Test (theo documentation) ===");

        // Test case 1: Minimal params theo VNPay docs
        Map<String, String> params1 = new TreeMap<>();
        params1.put("vnp_Version", "2.1.0");
        params1.put("vnp_Command", "pay");
        params1.put("vnp_TmnCode", "DEMOV210");
        params1.put("vnp_Amount", "10000000");
        params1.put("vnp_CurrCode", "VND");
        params1.put("vnp_TxnRef", "12345678");
        params1.put("vnp_OrderInfo", "Thanh toan don hang");
        params1.put("vnp_OrderType", "other");
        params1.put("vnp_Locale", "vn");
        params1.put("vnp_ReturnUrl", "https://domainmerchant.vn/ReturnUrl");
        params1.put("vnp_IpAddr", "127.0.0.1");
        params1.put("vnp_CreateDate", "20210801153333");

        String hashData1 = buildHashData(params1);
        System.out.println("Test 1 - Hash Data: " + hashData1);

        String secretKey1 = "RAOEXHYVSDDIIENYWSLDIIZTANRUAXNG";
        String hash1 = hmacSHA512(secretKey1, hashData1);
        System.out.println("Test 1 - Hash: " + hash1);

        System.out.println("\n=== Test với sandbox credentials ===");

        // Test case 2: Với sandbox credentials
        Map<String, String> params2 = new TreeMap<>();
        params2.put("vnp_Version", "2.1.0");
        params2.put("vnp_Command", "pay");
        params2.put("vnp_TmnCode", "ZAVGV1VT");
        params2.put("vnp_Amount", "10000000");
        params2.put("vnp_CurrCode", "VND");
        params2.put("vnp_TxnRef", "TEST123456");
        params2.put("vnp_OrderInfo", "Test payment");
        params2.put("vnp_OrderType", "other");
        params2.put("vnp_Locale", "vn");
        params2.put("vnp_ReturnUrl", "http://localhost:8080/api/payment/vn-pay-callback");
        params2.put("vnp_IpAddr", "127.0.0.1");
        params2.put("vnp_CreateDate", "20250625140000");

        String hashData2 = buildHashData(params2);
        System.out.println("Test 2 - Hash Data: " + hashData2);

        String secretKey2 = "OR92SDL9CRPL5TOXFICMKRVASZ4FXJ4M";
        String hash2 = hmacSHA512(secretKey2, hashData2);
        System.out.println("Test 2 - Hash: " + hash2);

        System.out.println("\n=== So sánh format ===");
        System.out.println("Hash 1 length: " + hash1.length());
        System.out.println("Hash 2 length: " + hash2.length());
        System.out.println("Hash 1 format: " + (hash1.equals(hash1.toUpperCase()) ? "UPPERCASE" : "Mixed/Lowercase"));
        System.out.println("Hash 2 format: " + (hash2.equals(hash2.toUpperCase()) ? "UPPERCASE" : "Mixed/Lowercase"));
    }

    public static String buildHashData(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                if (!first) {
                    hashData.append("&");
                }
                hashData.append(entry.getKey()).append("=").append(value);
                first = false;
            }
        }
        return hashData.toString();
    }
}
