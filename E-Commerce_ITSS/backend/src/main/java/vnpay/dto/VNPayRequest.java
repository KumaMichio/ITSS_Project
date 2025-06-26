package vnpay.dto;

/**
 * VNPay Payment Request DTO
 */
public class VNPayRequest {
    private long amount;
    private String bankCode;
    private String language;
    private String orderInfo;
    private String orderType;

    // Constructors
    public VNPayRequest() {
    }

    public VNPayRequest(long amount, String bankCode, String language, String orderInfo, String orderType) {
        this.amount = amount;
        this.bankCode = bankCode;
        this.language = language;
        this.orderInfo = orderInfo;
        this.orderType = orderType;
    }

    // Getters and Setters
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
