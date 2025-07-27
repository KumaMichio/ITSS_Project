package services;

import models.*;
import dtos.GuestDeliveryInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestOrderService {

    private final OrderServiceV2 orderServiceV2;

    /**
     * Create order for guest user (no login required)
     */
    public Order createGuestOrder(List<Integer> productIds, List<Integer> quantities,
            GuestDeliveryInfoDTO deliveryInfo, boolean isExpress) {

        // Validate inputs
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs cannot be empty");
        }
        if (quantities == null || quantities.size() != productIds.size()) {
            throw new IllegalArgumentException("Quantities must match product IDs count");
        }
        if (deliveryInfo == null) {
            throw new IllegalArgumentException("Delivery information is required");
        }

        // Use OrderServiceV2 to create guest order
        return orderServiceV2.createGuestOrder(
                productIds,
                quantities,
                deliveryInfo.getEmail(),
                deliveryInfo.getName(),
                deliveryInfo.getPhone(),
                deliveryInfo.getAddress(),
                deliveryInfo.getProvince(),
                isExpress);
    }

    /**
     * Calculate shipping preview for guest checkout
     */
    public ShippingPreview calculateShippingPreview(List<Integer> productIds, List<Integer> quantities,
            String province, String address, boolean isExpress) {

        double subtotal = 0;
        for (int i = 0; i < productIds.size(); i++) {
            // Simple calculation - in real implementation, get actual product prices
            subtotal += quantities.get(i) * 100000; // Default price
        }

        // Check if express delivery is available using proper logic
        boolean expressAvailable = isExpressAvailable(province, address);
        double shippingFee = calculateShippingFee(province, subtotal, isExpress && expressAvailable);
        double total = subtotal + shippingFee;

        return new ShippingPreview(subtotal, shippingFee, total, isExpress && expressAvailable, expressAvailable);
    }

    private double calculateShippingFee(String province, double subtotal, boolean isExpress) {
        double baseFee = isExpress ? 30000 : 22000;

        // Add province-based fee
        if (province != null && (province.contains("Hà Nội"))) {
            return baseFee;
        } else {
            return baseFee + 12000;
        }
    }

    /**
     * Check if express delivery is available for the given address
     */
    private boolean isExpressAvailable(String province, String address) {
        // Check if province is Hanoi
        if (province == null || (!province.equalsIgnoreCase("Hà Nội") && !province.equalsIgnoreCase("Hanoi"))) {
            return false;
        }

        // Check if address is in inner city districts
        String[] innerCityDistricts = {
                "Ba Đình", "Hoàn Kiếm", "Tây Hồ", "Long Biên", "Cầu Giấy",
                "Đống Đa", "Hai Bà Trưng", "Hoàng Mai", "Thanh Xuân",
                "Sóc Sơn", "Đông Anh", "Gia Lâm", "Nam Từ Liêm", "Bắc Từ Liêm"
        };

        if (address != null) {
            for (String district : innerCityDistricts) {
                if (address.toLowerCase().contains(district.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static class ShippingPreview {
        private final double subtotal;
        private final double shippingFee;
        private final double total;
        private final boolean isExpress;
        private final boolean expressAvailable;

        public ShippingPreview(double subtotal, double shippingFee, double total, boolean isExpress) {
            this.subtotal = subtotal;
            this.shippingFee = shippingFee;
            this.total = total;
            this.isExpress = isExpress;
            this.expressAvailable = true; // Default for backward compatibility
        }

        public ShippingPreview(double subtotal, double shippingFee, double total, boolean isExpress,
                boolean expressAvailable) {
            this.subtotal = subtotal;
            this.shippingFee = shippingFee;
            this.total = total;
            this.isExpress = isExpress;
            this.expressAvailable = expressAvailable;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public double getShippingFee() {
            return shippingFee;
        }

        public double getTotal() {
            return total;
        }

        public boolean isExpress() {
            return isExpress;
        }

        public boolean isExpressAvailable() {
            return expressAvailable;
        }
    }
}
