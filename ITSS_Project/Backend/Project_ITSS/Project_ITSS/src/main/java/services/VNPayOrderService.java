package services;

import config.VNPayConstant;
import dtos.PaymentDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import util.VNPayUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayOrderService {

    public Map<String, Object> createVNPayOrder(HttpServletRequest request, PaymentDTO.VNPayRequest orderRequest)
            throws UnsupportedEncodingException {

        Map<String, Object> payload = new HashMap<>() {
            {
                put("vnp_Version", VNPayConstant.VNP_VERSION);
                put("vnp_Command", VNPayConstant.VNP_COMMAND_ORDER);
                put("vnp_TmnCode", VNPayConstant.VNP_TMN_CODE);
                put("vnp_Amount", String.valueOf(orderRequest.getAmount() * 100));
                put("vnp_CurrCode", VNPayConstant.VNP_CURRENCY_CODE);
                put("vnp_TxnRef", VNPayUtil.getRandomNumber(8));
                put("vnp_OrderInfo",
                        orderRequest.getOrderInfo() != null ? orderRequest.getOrderInfo() : "Thanh toan don hang");
                put("vnp_OrderType", VNPayConstant.ORDER_TYPE);
                put("vnp_Locale",
                        orderRequest.getLanguage() != null ? orderRequest.getLanguage() : VNPayConstant.VNP_LOCALE);
                put("vnp_ReturnUrl", VNPayConstant.VNP_RETURN_URL);
                put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
                put("vnp_CreateDate", VNPayUtil.generateDate(false));
                put("vnp_ExpireDate", VNPayUtil.generateDate(true));
            }
        };

        // Add bank code if provided
        if (orderRequest.getBankCode() != null && !orderRequest.getBankCode().isEmpty()) {
            payload.put("vnp_BankCode", orderRequest.getBankCode());
        }

        String queryUrl = getQueryUrl(payload).get("queryUrl")
                + "&vnp_SecureHash="
                + VNPayUtil.hmacSHA512(VNPayConstant.SECRET_KEY, getQueryUrl(payload).get("hashData"));

        String paymentUrl = VNPayConstant.VNP_PAY_URL + "?" + queryUrl;
        payload.put("redirect_url", paymentUrl);

        return payload;
    }

    private Map<String, String> getQueryUrl(Map<String, Object> payload) throws UnsupportedEncodingException {

        List<String> fieldNames = new ArrayList<>(payload.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = (String) payload.get(fieldName);
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

        return new HashMap<>() {
            {
                put("queryUrl", query.toString());
                put("hashData", hashData.toString());
            }
        };
    }
}
