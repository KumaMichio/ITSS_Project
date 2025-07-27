package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import config.VNPayConfig;
import util.VNPayUtil;
import vnpay.dto.VNPayRequest;
import vnpay.dto.VNPayResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Enhanced VNPay Service integrating with existing payment system
 */
@Service
public class VNPayIntegrationService {

    @Autowired
    private VNPayConfig vnPayConfig;

    // Constants từ VNPayController hiện tại
    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_CURRENCY_CODE = "VND";
    private static final String VNP_LOCALE = "vn";

    /**
     * Tạo URL thanh toán VNPay với order ID và amount
     */
    public String createVNPayPaymentUrl(Long orderId, Long amount, String orderInfo, HttpServletRequest request)
            throws UnsupportedEncodingException {

        String vnp_TxnRef = String.valueOf(orderId);
        String vnp_IpAddr = VNPayUtil.getIpAddress(request);
        String vnp_TmnCode = vnPayConfig.getTmnCode();

        Map<String, String> vnp_Params = new HashMap<>();
        // Debug logging
        System.out.println("=== VNPayIntegrationService Debug ===");
        System.out.println("Input amount: " + amount);
        System.out.println("Amount * 100 for VNPay: " + (amount * 100));
        System.out.println("====================================");

        vnp_Params.put("vnp_Version", VNP_VERSION);
        vnp_Params.put("vnp_Command", VNP_COMMAND);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Convert to VND cents
        vnp_Params.put("vnp_CurrCode", VNP_CURRENCY_CODE);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef); // Use actual order ID
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", VNP_LOCALE);
        vnp_Params.put("vnp_ReturnUrl", "http://localhost:5173/transaction-result"); // Frontend result page
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnPayConfig.getUrl() + "?" + queryUrl;
    }

    /**
     * Tạo URL thanh toán với VNPayRequest
     */
    public VNPayResponse createPaymentUrl(VNPayRequest request, HttpServletRequest httpRequest) {
        try {
            String paymentUrl = createVNPayPaymentUrl(
                    Long.valueOf(VNPayUtil.getRandomNumber(8)),
                    request.getAmount(),
                    request.getOrderInfo() != null ? request.getOrderInfo() : "Thanh toan don hang",
                    httpRequest);
            return VNPayResponse.success(paymentUrl);
        } catch (Exception e) {
            return VNPayResponse.error("Error creating payment URL: " + e.getMessage());
        }
    }

    /**
     * Validate VNPay response signature
     */
    public boolean validateResponse(Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            Map<String, String> paramsToVerify = new HashMap<>(params);
            paramsToVerify.remove("vnp_SecureHashType");
            paramsToVerify.remove("vnp_SecureHash");

            String signValue = hashAllFields(paramsToVerify);
            return signValue.equals(vnp_SecureHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate VNPay hash for verification
     */
    public String generateVNPayHash(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(fieldValue);
                if (itr.hasNext()) {
                    hashData.append("&");
                }
            }
        }

        return VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
    }

    /**
     * Hash all fields utility method
     */
    private String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), sb.toString());
    }
}
