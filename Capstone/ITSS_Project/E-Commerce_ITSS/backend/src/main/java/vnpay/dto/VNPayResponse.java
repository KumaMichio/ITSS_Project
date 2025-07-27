package vnpay.dto;

/**
 * VNPay Payment Response DTO
 */
public class VNPayResponse {
    private String code;
    private String message;
    private String data;

    // Constructors
    public VNPayResponse() {
    }

    public VNPayResponse(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Static factory methods
    public static VNPayResponse success(String paymentUrl) {
        return new VNPayResponse("00", "success", paymentUrl);
    }

    public static VNPayResponse error(String message) {
        return new VNPayResponse("01", message, null);
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
