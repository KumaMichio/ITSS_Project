package controllers;

import models.Order;
import services.GuestOrderService;
import services.StockValidationService;
import dtos.GuestDeliveryInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuestOrderController {

    private final GuestOrderService guestOrderService;
    private final StockValidationService stockValidationService;

    /**
     * Validate stock for products before checkout
     */
    @PostMapping("/validate-stock")
    public ResponseEntity<?> validateStock(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> productIds = (List<Integer>) request.get("productIds");
            @SuppressWarnings("unchecked")
            List<Integer> quantities = (List<Integer>) request.get("quantities");

            StockValidationService.StockValidationResult result = stockValidationService.validateCartStock(productIds,
                    quantities);

            Map<String, Object> response = new HashMap<>();
            response.put("isValid", result.isValid());
            response.put("errors", result.getErrors());
            response.put("availableStock", result.getAvailableStock());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("isValid", false);
            errorResponse.put("errors", List.of("Lỗi khi kiểm tra tồn kho: " + e.getMessage()));
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Calculate shipping preview for guest checkout
     */
    @PostMapping("/shipping-preview")
    public ResponseEntity<?> getShippingPreview(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> productIds = (List<Integer>) request.get("productIds");
            @SuppressWarnings("unchecked")
            List<Integer> quantities = (List<Integer>) request.get("quantities");
            String province = (String) request.get("province");
            String address = (String) request.get("address");
            Boolean isExpress = (Boolean) request.get("isExpress");

            if (isExpress == null)
                isExpress = false;

            GuestOrderService.ShippingPreview preview = guestOrderService.calculateShippingPreview(
                    productIds, quantities, province, address, isExpress);

            Map<String, Object> response = new HashMap<>();
            response.put("regularShippingFee", preview.getRegularShippingFee());
            response.put("expressShippingFee", preview.getExpressShippingFee());
            response.put("expressAvailable", preview.isExpressAvailable());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi tính phí vận chuyển: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Create guest order (no login required)
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createGuestOrder(@RequestBody GuestOrderRequest request) {
        try {
            // Validate input
            if (request.getProductIds() == null || request.getProductIds().isEmpty()) {
                throw new IllegalArgumentException("Product IDs cannot be empty");
            }

            if (request.getQuantities() == null || request.getQuantities().size() != request.getProductIds().size()) {
                throw new IllegalArgumentException("Quantities must match product IDs count");
            }

            if (request.getDeliveryInfo() == null) {
                throw new IllegalArgumentException("Delivery information is required");
            } // Validate delivery info
            GuestDeliveryInfoDTO deliveryInfo = request.getDeliveryInfo();
            if (deliveryInfo.getName() == null || deliveryInfo.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (deliveryInfo.getEmail() == null || deliveryInfo.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (deliveryInfo.getPhone() == null || deliveryInfo.getPhone().trim().isEmpty()) {
                throw new IllegalArgumentException("Phone is required");
            }
            if (deliveryInfo.getAddress() == null || deliveryInfo.getAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("Address is required");
            }
            if (deliveryInfo.getProvince() == null || deliveryInfo.getProvince().trim().isEmpty()) {
                throw new IllegalArgumentException("Province is required");
            }

            // Create order
            Order order = guestOrderService.createGuestOrder(
                    request.getProductIds(),
                    request.getQuantities(),
                    request.getDeliveryInfo(),
                    request.isExpress());

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getOrderId());
            response.put("totalAmount", order.getTotalAmount());
            response.put("shippingFees", order.getShippingFees());
            response.put("totalFees", order.getTotalFees());
            response.put("message", "Đơn hàng đã được tạo thành công!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Request class for guest order creation
     */
    public static class GuestOrderRequest {
        private List<Integer> productIds;
        private List<Integer> quantities;
        private GuestDeliveryInfoDTO deliveryInfo;
        private boolean express;

        // Constructors
        public GuestOrderRequest() {
        }

        public GuestOrderRequest(List<Integer> productIds, List<Integer> quantities,
                GuestDeliveryInfoDTO deliveryInfo, boolean express) {
            this.productIds = productIds;
            this.quantities = quantities;
            this.deliveryInfo = deliveryInfo;
            this.express = express;
        }

        // Getters and Setters
        public List<Integer> getProductIds() {
            return productIds;
        }

        public void setProductIds(List<Integer> productIds) {
            this.productIds = productIds;
        }

        public List<Integer> getQuantities() {
            return quantities;
        }

        public void setQuantities(List<Integer> quantities) {
            this.quantities = quantities;
        }

        public GuestDeliveryInfoDTO getDeliveryInfo() {
            return deliveryInfo;
        }

        public void setDeliveryInfo(GuestDeliveryInfoDTO deliveryInfo) {
            this.deliveryInfo = deliveryInfo;
        }

        public boolean isExpress() {
            return express;
        }

        public void setExpress(boolean express) {
            this.express = express;
        }
    }
}
