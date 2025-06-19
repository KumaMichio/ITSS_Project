package dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PaymentDTO {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class VNPayRequest {
        private int orderId;
        private Long amount; // Changed to Long to match mẫu
        private String orderInfo;
        private String bankCode;
        private String language; // Added language support
    }
}
