package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Standalone VNPay Hash Test - không cần Spring dependencies
public class VNPayDebugTest {

    // Copy method từ VNPayUtil để test độc lập
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
            return sb.toString().toUpperCase(); // VNPay yêu cầu UPPERCASE
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate HMAC SHA-512 signature", ex);
        }
    }

    public static String buildHashData(Map<String, String> params) {
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        StringBuilder hashData = new StringBuilder();
        for (String key : sortedKeys) {
            String value = params.get(key);
            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                }
                try {
                    // FIX: URL-encode the value to handle special characters and spaces
                    hashData.append(key).append("=")
                            .append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    // This should not happen with UTF-8
                    throw new RuntimeException("Failed to encode value for hashing", e);
                }
            }
        }
        return hashData.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== VNPay Hash Debug Test ===");

        // Test data giống như VNPay documentation
        // Use HashMap as we sort the keys explicitly anyway
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", "ZAVGV1VT");
        params.put("vnp_Amount", "10000000"); // 100,000 VND * 100
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", "12345678");
        params.put("vnp_OrderInfo", "Thanh toan don hang");
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", "http://localhost:8080/api/payment/vn-pay-callback");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", "20250625140000");
        params.put("vnp_ExpireDate", "20250625141500");

        // Sort parameters
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        System.out.println("Sorted keys: " + sortedKeys);

        String hashString = buildHashData(params);
        System.out.println("Hash data string: " + hashString);
        System.out.println("Hash data length: " + hashString.length());

        // Generate hash
        String secretKey = "OR92SDL9CRPL5TOXFICMKRVASZ4FXJ4M";
        String hash = hmacSHA512(secretKey, hashString);

        System.out.println("Secret key: " + secretKey);
        System.out.println("Generated hash: " + hash);
        System.out.println("Hash length: " + hash.length());

        // Test với data khác
        System.out.println("\n=== Test with minimal data ===");
        Map<String, String> minimalParams = new HashMap<>();
        minimalParams.put("vnp_Amount", "10000000");
        minimalParams.put("vnp_Command", "pay");
        minimalParams.put("vnp_TmnCode", "ZAVGV1VT");
        minimalParams.put("vnp_Version", "2.1.0");

        String minimalHash = buildHashData(minimalParams);
        System.out.println("Minimal hash data: " + minimalHash);
        System.out.println("Minimal hash: " + hmacSHA512(secretKey, minimalHash));
    }
}
