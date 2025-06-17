package com.truonggiang.vnpay.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VnPayConstant {

    @Value("${vnpay.pay-url}")
    public static String VNP_PAY_URL;

    @Value("${vnpay.return-url}")
    public static String VNP_RETURN_URL;

    @Value("${vnpay.tmn-code}")
    public static String VNP_TMN_CODE;

    @Value("${vnpay.secret-key}")
    public static String SECRET_KEY;

    @Value("${vnpay.api-url}")
    public static String VNP_API_URL;

    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND_ORDER = "pay";
    public static final String VNP_COMMAND_STATUS = "querydr";
    public static final String ORDER_TYPE = "other";
    public static final String VNP_CURRENCY_CODE = "VND";
    public static final String VNP_LOCALE = "vn";
    public static final String VNP_IP_ADDR = "192.168.1.252";
    public static final String VNP_BANK_CODE = "NCB";
    public static final String VNP_RESPONSE_CODE = "00";
    public static final String VNP_TRANSACTION_STATUS = "00";

    @Value("${vnpay.pay-url}")
    public void setVnpPayUrl(String vnpPayUrl) {
        VNP_PAY_URL = vnpPayUrl;
    }

    @Value("${vnpay.return-url}")
    public void setVnpReturnUrl(String vnpReturnUrl) {
        VNP_RETURN_URL = vnpReturnUrl;
    }

    @Value("${vnpay.tmn-code}")
    public void setVnpTmnCode(String vnpTmnCode) {
        VNP_TMN_CODE = vnpTmnCode;
    }

    @Value("${vnpay.secret-key}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    @Value("${vnpay.api-url}")
    public void setVnpApiUrl(String vnpApiUrl) {
        VNP_API_URL = vnpApiUrl;
    }
}
