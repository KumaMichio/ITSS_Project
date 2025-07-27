package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class CartValidationDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItem {
        private int productId;
        private int quantity;
        private double price;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidationResult {
        private boolean isValid;
        private List<String> errors;
        private double totalPrice;
        private double updatedTotalPrice; // Giá sau khi cập nhật
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidationRequest {
        private List<CartItem> items;
    }
}
