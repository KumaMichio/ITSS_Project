package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Test case để so sánh với VNPay documentation và chương trình test mẫu
 * Kiểm tra từng bước hash generation theo đúng chuẩn VNPay
 */
public class VNPayComparisonTest {

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

    public static String buildHashData(Map<String, String> params) {
        // Remove vnp_SecureHashType if present (CRITICAL!)
        Map<String, String> filteredParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"vnp_SecureHashType".equals(entry.getKey()) &&
                    entry.getValue() != null && !entry.getValue().isEmpty()) {
                filteredParams.put(entry.getKey(), entry.getValue());
            }
        }

        // Build hash data string
        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : filteredParams.entrySet()) {
            if (!first) {
                hashData.append("&");
            }
            hashData.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return hashData.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== VNPay Comparison Test - So sánh với mẫu chuẩn ===");
        System.out.println();

        // Test Case 1: Theo VNPay documentation example
        System.out.println("TEST CASE 1: VNPay Documentation Example");
        System.out.println("----------------------------------------");

        Map<String, String> docParams = new TreeMap<>();
        docParams.put("vnp_Version", "2.1.0");
        docParams.put("vnp_Command", "pay");
        docParams.put("vnp_TmnCode", "DEMOV210");
        docParams.put("vnp_Amount", "10000000");
        docParams.put("vnp_CurrCode", "VND");
        docParams.put("vnp_TxnRef", "12345678");
        docParams.put("vnp_OrderInfo", "Thanh toan don hang");
        docParams.put("vnp_OrderType", "other");
        docParams.put("vnp_Locale", "vn");
        docParams.put("vnp_ReturnUrl", "https://domainmerchant.vn/ReturnUrl");
        docParams.put("vnp_IpAddr", "127.0.0.1");
        docParams.put("vnp_CreateDate", "20210801153333");

        String docHashData = buildHashData(docParams);
        String docSecretKey = "RAOEXHYVSDDIIENYWSLDIIZTANRUAXNG";
        String docHash = hmacSHA512(docSecretKey, docHashData);

        System.out.println("Params count: " + docParams.size());
        System.out.println("Secret key: " + docSecretKey);
        System.out.println("Hash data: " + docHashData);
        System.out.println("Hash data length: " + docHashData.length());
        System.out.println("Generated hash: " + docHash);
        System.out.println("Hash length: " + docHash.length());
        System.out.println();

        // Test Case 2: Sandbox credentials
        System.out.println("TEST CASE 2: Current Sandbox Credentials");
        System.out.println("----------------------------------------");

        Map<String, String> sandboxParams = new TreeMap<>();
        sandboxParams.put("vnp_Version", "2.1.0");
        sandboxParams.put("vnp_Command", "pay");
        sandboxParams.put("vnp_TmnCode", "ZAVGV1VT");
        sandboxParams.put("vnp_Amount", "10000000");
        sandboxParams.put("vnp_CurrCode", "VND");
        sandboxParams.put("vnp_TxnRef", "12345678");
        sandboxParams.put("vnp_OrderInfo", "Thanh toan don hang");
        sandboxParams.put("vnp_OrderType", "other");
        sandboxParams.put("vnp_Locale", "vn");
        sandboxParams.put("vnp_ReturnUrl", "http://localhost:8080/api/payment/vn-pay-callback");
        sandboxParams.put("vnp_IpAddr", "127.0.0.1");
        sandboxParams.put("vnp_CreateDate", "20250625140000");
        sandboxParams.put("vnp_ExpireDate", "20250625141500");

        String sandboxHashData = buildHashData(sandboxParams);
        String sandboxSecretKey = "OR92SDL9CRPL5TOXFICMKRVASZ4FXJ4M";
        String sandboxHash = hmacSHA512(sandboxSecretKey, sandboxHashData);

        System.out.println("Params count: " + sandboxParams.size());
        System.out.println("Secret key: " + sandboxSecretKey);
        System.out.println("Hash data: " + sandboxHashData);
        System.out.println("Hash data length: " + sandboxHashData.length());
        System.out.println("Generated hash: " + sandboxHash);
        System.out.println("Hash length: " + sandboxHash.length());
        System.out.println();

        // Test Case 3: Test với vnp_SecureHashType included (sai cách)
        System.out.println("TEST CASE 3: Wrong - With vnp_SecureHashType in hash");
        System.out.println("---------------------------------------------------");

        Map<String, String> wrongParams = new TreeMap<>();
        wrongParams.putAll(sandboxParams);
        wrongParams.put("vnp_SecureHashType", "HMACSHA512"); // Thêm param này (SAI!)

        // Build hash DATA KHÔNG loại bỏ vnp_SecureHashType (để test lỗi)
        StringBuilder wrongHashData = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : wrongParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (!first) {
                    wrongHashData.append("&");
                }
                wrongHashData.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }

        String wrongHash = hmacSHA512(sandboxSecretKey, wrongHashData.toString());

        System.out.println("Params count (with vnp_SecureHashType): " + wrongParams.size());
        System.out.println("Hash data (WRONG): " + wrongHashData.toString());
        System.out.println("Hash data length: " + wrongHashData.length());
        System.out.println("Generated hash (WRONG): " + wrongHash);
        System.out.println("Hash length: " + wrongHash.length());
        System.out.println();

        // So sánh
        System.out.println("COMPARISON RESULTS");
        System.out.println("==================");
        System.out.println("Test 2 vs Test 3 hashes match: " + sandboxHash.equals(wrongHash));
        System.out.println("Test 2 hash data length: " + sandboxHashData.length());
        System.out.println("Test 3 hash data length: " + wrongHashData.length());
        System.out.println();

        // Validation checks
        System.out.println("VALIDATION CHECKS");
        System.out.println("=================");
        System.out.println("All hashes are 128 chars: " +
                (docHash.length() == 128 && sandboxHash.length() == 128 && wrongHash.length() == 128));
        System.out.println("All hashes are UPPERCASE: " +
                (docHash.equals(docHash.toUpperCase()) &&
                        sandboxHash.equals(sandboxHash.toUpperCase()) &&
                        wrongHash.equals(wrongHash.toUpperCase())));
        System.out.println("Hash format (hex): " +
                (docHash.matches("[A-F0-9]+") &&
                        sandboxHash.matches("[A-F0-9]+") &&
                        wrongHash.matches("[A-F0-9]+")));
    }
}
