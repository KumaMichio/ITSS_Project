package dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;

public class PaymentDTO {
    @AllArgsConstructor
    @Builder
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
    }

}
