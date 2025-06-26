package config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import util.VNPayUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class VNPayConfig {
    @Getter
    @Value("${vnPay.url}")
    private String vnp_PayUrl;

    @Value("${vnPay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnPay.tmnCode}")
    private String vnp_TmnCode;

    @Getter
    @Value("${vnPay.secretKey}")
    private String secretKey;

    @Value("${vnPay.version}")
    private String vnp_Version;

    @Value("${vnPay.command}")
    private String vnp_Command;

    @Value("${vnPay.orderType}")
    private String orderType;

    @Value("${vnPay.currCode}")
    private String currCode;

    @Value("${vnPay.locale}")
    private String locale;

    @Value("${vnPay.apiUrl}")
    private String apiUrl;

    @Value("${vnPay.timeoutExpress}")
    private int timeoutExpress;

    public Map<String, String> getVNPayConfig(int orderId) {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", this.currCode);
        vnpParamsMap.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + orderId);
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", this.locale);
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);

        calendar.add(Calendar.MINUTE, this.timeoutExpress);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        // Add SecureHashType for VNPay signature
        vnpParamsMap.put("vnp_SecureHashType", "HMACSHA512");

        return vnpParamsMap;
    }

    // Getter methods for additional properties
    public String getApiUrl() {
        return apiUrl;
    }

    public String getUrl() {
        return vnp_PayUrl;
    }

    // Getter methods for additional properties
    public String getTmnCode() {
        return vnp_TmnCode;
    }

    public String getReturnUrl() {
        return vnp_ReturnUrl;
    }

    public String getVersion() {
        return vnp_Version;
    }

    public String getCommand() {
        return vnp_Command;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getCurrCode() {
        return currCode;
    }

    public String getLocale() {
        return locale;
    }

    public int getTimeoutExpress() {
        return timeoutExpress;
    }
}
