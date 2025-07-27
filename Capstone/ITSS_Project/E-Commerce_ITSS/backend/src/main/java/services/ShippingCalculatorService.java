package services;

import models.DeliveryInformation;
import models.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingCalculatorService {

    // Constants for shipping fees
    private static final int HANOI_HCMC_BASE_FEE = 22000; // 22,000 VND for first 3kg
    private static final int OTHER_PROVINCE_BASE_FEE = 30000; // 30,000 VND for first 0.5kg
    private static final int ADDITIONAL_FEE_PER_500G = 2500; // 2,500 VND per 0.5kg
    private static final int EXPRESS_FEE_PER_ITEM = 10000; // 10,000 VND per item for express
    private static final int FREE_SHIPPING_THRESHOLD = 100000; // 100,000 VND
    private static final int MAX_FREE_SHIPPING_DISCOUNT = 25000; // Max 25,000 VND discount
    private static final double HANOI_HCMC_WEIGHT_THRESHOLD = 3.0; // 3kg
    private static final double OTHER_PROVINCE_WEIGHT_THRESHOLD = 0.5; // 0.5kg

    /**
     * Calculate shipping fee based on delivery address and products
     */
    public int calculateShippingFee(DeliveryInformation deliveryInfo, List<Product> products,
            List<Integer> quantities, boolean isExpress) {

        double totalWeight = calculateTotalWeight(products, quantities);
        int baseFee = calculateBaseFee(deliveryInfo.getProvince(), totalWeight);

        // Add express fee if applicable
        if (isExpress) {
            int expressCharge = products.size() * EXPRESS_FEE_PER_ITEM;
            baseFee += expressCharge;
        }

        return baseFee;
    }

    /**
     * Calculate shipping fee with free shipping discount applied
     */
    public int calculateShippingFeeWithDiscount(DeliveryInformation deliveryInfo, List<Product> products,
            List<Integer> quantities, boolean isExpress, int totalAmount) {

        int baseFee = calculateShippingFee(deliveryInfo, products, quantities, isExpress);

        // Apply free shipping discount for orders over 100,000 VND (not for express)
        if (totalAmount >= FREE_SHIPPING_THRESHOLD && !isExpress) {
            int discount = Math.min(baseFee, MAX_FREE_SHIPPING_DISCOUNT);
            baseFee -= discount;
        }

        return Math.max(0, baseFee); // Ensure fee is not negative
    }

    /**
     * Check if express delivery is available for the given address and products
     */
    public boolean isExpressDeliveryAvailable(DeliveryInformation deliveryInfo, List<Product> products) {
        // Express delivery only available in Hanoi inner city
        if (!isHanoiInnerCity(deliveryInfo)) {
            return false;
        }

        // Check if all products are eligible for express delivery (products with ID
        // 1-10)
        for (Product product : products) {
            if (product.getId() < 1 || product.getId() > 10) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the delivery address is in Hanoi inner city
     */
    private boolean isHanoiInnerCity(DeliveryInformation deliveryInfo) {
        String province = deliveryInfo.getProvince();
        String address = deliveryInfo.getAddress();

        // Check if province is Hanoi
        if (!province.equalsIgnoreCase("Hà Nội") && !province.equalsIgnoreCase("Hanoi")) {
            return false;
        }

        // Check if address is in inner city districts
        String[] innerCityDistricts = {
                "Ba Đình", "Hoàn Kiếm", "Tây Hồ", "Long Biên", "Cầu Giấy",
                "Đống Đa", "Hai Bà Trưng", "Hoàng Mai", "Thanh Xuân",
                "Sóc Sơn", "Đông Anh", "Gia Lâm", "Nam Từ Liêm", "Bắc Từ Liêm"
        };

        for (String district : innerCityDistricts) {
            if (address.toLowerCase().contains(district.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calculate total weight of all products
     */
    private double calculateTotalWeight(List<Product> products, List<Integer> quantities) {
        double totalWeight = 0;
        for (int i = 0; i < products.size(); i++) {
            totalWeight += products.get(i).getWeight() * quantities.get(i);
        }
        return totalWeight;
    }

    /**
     * Calculate base shipping fee based on location and weight
     */
    private int calculateBaseFee(String province, double totalWeight) {
        boolean isHanoiOrHCMC = province.equalsIgnoreCase("Hà Nội") ||
                province.equalsIgnoreCase("Hanoi") ||
                province.equalsIgnoreCase("Hồ Chí Minh") ||
                province.equalsIgnoreCase("Ho Chi Minh City");

        if (isHanoiOrHCMC) {
            // Hanoi/HCMC: 22,000 VND for first 3kg
            if (totalWeight <= HANOI_HCMC_WEIGHT_THRESHOLD) {
                return HANOI_HCMC_BASE_FEE;
            } else {
                double extraWeight = totalWeight - HANOI_HCMC_WEIGHT_THRESHOLD;
                int extraFee = (int) (Math.ceil(extraWeight / 0.5) * ADDITIONAL_FEE_PER_500G);
                return HANOI_HCMC_BASE_FEE + extraFee;
            }
        } else {
            // Other provinces: 30,000 VND for first 0.5kg
            if (totalWeight <= OTHER_PROVINCE_WEIGHT_THRESHOLD) {
                return OTHER_PROVINCE_BASE_FEE;
            } else {
                double extraWeight = totalWeight - OTHER_PROVINCE_WEIGHT_THRESHOLD;
                int extraFee = (int) (Math.ceil(extraWeight / 0.5) * ADDITIONAL_FEE_PER_500G);
                return OTHER_PROVINCE_BASE_FEE + extraFee;
            }
        }
    }

    /**
     * Get shipping fee breakdown for display
     */
    public String getShippingFeeBreakdown(DeliveryInformation deliveryInfo, List<Product> products,
            List<Integer> quantities, boolean isExpress, int totalAmount) {

        double totalWeight = calculateTotalWeight(products, quantities);
        int baseFee = calculateBaseFee(deliveryInfo.getProvince(), totalWeight);

        StringBuilder breakdown = new StringBuilder();
        breakdown.append("Phí vận chuyển cơ bản: ").append(baseFee).append(" VND\n");
        breakdown.append("Tổng trọng lượng: ").append(String.format("%.2f", totalWeight)).append(" kg\n");

        if (isExpress) {
            int expressCharge = products.size() * EXPRESS_FEE_PER_ITEM;
            breakdown.append("Phí giao hàng nhanh: ").append(expressCharge).append(" VND\n");
            baseFee += expressCharge;
        }

        if (totalAmount >= FREE_SHIPPING_THRESHOLD && !isExpress) {
            int discount = Math.min(baseFee, MAX_FREE_SHIPPING_DISCOUNT);
            breakdown.append("Giảm giá miễn phí vận chuyển: -").append(discount).append(" VND\n");
            baseFee -= discount;
        }

        breakdown.append("Tổng phí vận chuyển: ").append(Math.max(0, baseFee)).append(" VND");

        return breakdown.toString();
    }
}
