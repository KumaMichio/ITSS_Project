package services;

import models.*;
import repositories.*;
import dtos.GuestDeliveryInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GuestOrderService {

    private final OrderRepository orderRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final ShippingCalculatorService shippingCalculatorService;
    private final EmailService emailService;
    private final StockValidationService stockValidationService;

    /**
     * Create order for guest user (no login required)
     */
    public Order createGuestOrder(List<Integer> productIds, List<Integer> quantities,
            DeliveryInformation deliveryInfo, boolean isExpress) {
        // 1. Validate stock availability
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            final int index = i; // Make effectively final
            Product product = productRepository.findById(productIds.get(index))
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productIds.get(index)));

            // Check stock
            if (!stockValidationService.validateStock(product, quantities.get(index))) {
                throw new RuntimeException("Insufficient stock for product: " + product.getTitle() +
                        ". Available: " + product.getQuantity() +
                        ", Requested: " + quantities.get(index));
            }

            products.add(product);
        }

        // 2. Check express delivery availability
        if (isExpress && !shippingCalculatorService.isExpressDeliveryAvailable(deliveryInfo, products)) {
            throw new RuntimeException("Express delivery is not available for this address or products");
        }

        // 3. Save delivery information
        DeliveryInformation savedDeliveryInfo = deliveryInfoRepository.save(deliveryInfo);

        // 4. Calculate totals
        int totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            int itemPrice = (int) (product.getPrice() * quantity);
            totalAmount += itemPrice;

            OrderItem orderItem = OrderItem.orderitemBuilder()
                    .productId(product.getId())
                    .quantity(quantity)
                    .price(itemPrice)
                    .build();

            orderItems.add(orderItem);
        }

        // 5. Calculate shipping fees
        int shippingFees = shippingCalculatorService.calculateShippingFeeWithDiscount(
                savedDeliveryInfo, products, quantities, isExpress, totalAmount);

        // 6. Get shipping method
        ShippingMethod shippingMethod = shippingMethodRepository.findById(isExpress ? 2 : 1)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));

        // 7. Create order (VAT không tính theo yêu cầu)
        int vat = 0; // No VAT according to requirements
        double totalFees = totalAmount + shippingFees + vat;

        Order order = Order.orderBuilder()
                .user(null) // Guest order - no user
                .deliveryInformation(savedDeliveryInfo)
                .shippingMethod(shippingMethod)
                .shippingFees(shippingFees)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .VAT(vat)
                .totalFees(totalFees)
                .build();

        // 8. Set order for each item
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setOrderItems(orderItems);

        // 9. Update stock quantities
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int newQuantity = product.getQuantity() - quantities.get(i);
            product.setQuantity(newQuantity);
            productRepository.save(product);
        }

        // 10. Save order
        Order savedOrder = orderRepository.save(order); // 11. Send confirmation email (for registered users only)
        // Guest users' emails are handled separately in the DTO method
        if (deliveryInfo.getUser() != null) {
            try {
                emailService.sendOrderConfirmationEmail(savedOrder);
            } catch (Exception e) {
                // Log error but don't fail the order creation
                System.err.println("Failed to send confirmation email: " + e.getMessage());
            }
        }

        return savedOrder;
    }

    /**
     * Create order for guest user using DTO (no login required)
     */
    public Order createGuestOrder(List<Integer> productIds, List<Integer> quantities,
            GuestDeliveryInfoDTO deliveryInfoDTO, boolean isExpress) {

        // Convert DTO to DeliveryInformation
        DeliveryInformation deliveryInfo = new DeliveryInformation();
        deliveryInfo.setName(deliveryInfoDTO.getName());
        deliveryInfo.setPhone(deliveryInfoDTO.getPhone());
        deliveryInfo.setAddress(deliveryInfoDTO.getAddress());
        deliveryInfo.setProvince(deliveryInfoDTO.getProvince());

        // Create order using existing method
        Order order = createGuestOrder(productIds, quantities, deliveryInfo, isExpress);

        // Send guest email confirmation with the email from DTO
        try {
            emailService.sendGuestOrderConfirmationEmail(order, deliveryInfoDTO.getEmail());
        } catch (Exception e) {
            // Log error but don't fail the order creation
            System.err.println("Failed to send guest confirmation email: " + e.getMessage());
        }

        return order;
    }

    /**
     * Calculate shipping preview for guest checkout
     */
    public ShippingPreview calculateShippingPreview(List<Integer> productIds, List<Integer> quantities,
            String province, String address, boolean isExpress) {

        // Create temporary delivery info for calculation
        DeliveryInformation tempDeliveryInfo = new DeliveryInformation();
        tempDeliveryInfo.setProvince(province);
        tempDeliveryInfo.setAddress(address);

        // Get products
        List<Product> products = new ArrayList<>();
        int totalAmount = 0;
        for (int i = 0; i < productIds.size(); i++) {
            final int index = i; // Make effectively final for lambda
            Product product = productRepository.findById(productIds.get(index))
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productIds.get(index)));
            products.add(product);
            totalAmount += product.getPrice() * quantities.get(index);
        }

        // Check express availability
        boolean expressAvailable = shippingCalculatorService.isExpressDeliveryAvailable(tempDeliveryInfo, products);

        // Calculate fees
        int regularShippingFee = shippingCalculatorService.calculateShippingFeeWithDiscount(
                tempDeliveryInfo, products, quantities, false, totalAmount);

        int expressShippingFee = expressAvailable ? shippingCalculatorService.calculateShippingFeeWithDiscount(
                tempDeliveryInfo, products, quantities, true, totalAmount) : -1;

        return new ShippingPreview(regularShippingFee, expressShippingFee, expressAvailable);
    }

    /**
     * Shipping preview result
     */
    public static class ShippingPreview {
        private final int regularShippingFee;
        private final int expressShippingFee;
        private final boolean expressAvailable;

        public ShippingPreview(int regularShippingFee, int expressShippingFee, boolean expressAvailable) {
            this.regularShippingFee = regularShippingFee;
            this.expressShippingFee = expressShippingFee;
            this.expressAvailable = expressAvailable;
        }

        // Getters
        public int getRegularShippingFee() {
            return regularShippingFee;
        }

        public int getExpressShippingFee() {
            return expressShippingFee;
        }

        public boolean isExpressAvailable() {
            return expressAvailable;
        }
    }
}
