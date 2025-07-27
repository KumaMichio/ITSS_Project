package services;

import dtos.OrderDTO;
import lombok.RequiredArgsConstructor;
import models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 implements IOrderService {

    private final OrderRepository orderRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final TransactionInformationRepository transactionInformationRepository;

    /**
     * Create guest order with customer info embedded
     */
    @Transactional
    public Order createGuestOrder(
            List<Integer> productIds,
            List<Integer> quantities,
            String customerEmail,
            String customerFullName,
            String customerPhone,
            String deliveryAddress,
            String deliveryProvince,
            boolean isExpress) {

        // Validate products and calculate subtotal
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (int index = 0; index < productIds.size(); index++) {
            final int i = index; // Make effectively final
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productIds.get(i)));

            int quantity = quantities.get(i);
            if (product.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getTitle());
            }

            BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));
            subtotalAmount = subtotalAmount.add(itemTotal);

            // Create order item (will be saved with order)
            OrderItem orderItem = OrderItem.orderitemBuilder()
                    .productId(product.getId())
                    .quantity(quantity)
                    .price(product.getPrice()) // Use original product price, not itemTotal
                    .build();
            orderItems.add(orderItem);
        }

        // Get shipping method
        ShippingMethod shippingMethod = getShippingMethod(isExpress);

        // Calculate shipping fees using ShippingMethod base fee + province adjustment
        BigDecimal shippingFees = calculateShippingFees(deliveryProvince, subtotalAmount, shippingMethod);
        BigDecimal totalAmount = subtotalAmount.add(shippingFees);

        // Create order
        Order order = Order.orderBuilder()
                .user(null) // Guest order
                .transactionId(null) // Will be set when payment is processed
                .shippingMethod(shippingMethod)
                .shippingFees(shippingFees)
                .totalAmount(totalAmount)
                .subtotalAmount(subtotalAmount)
                .createdAt(LocalDateTime.now())
                .isPayment(false) // Not paid yet
                .customerEmail(customerEmail)
                .customerFullName(customerFullName)
                .customerPhone(customerPhone)
                .deliveryAddress(deliveryAddress)
                .deliveryProvince(deliveryProvince)
                .build();

        // Step 1: Save order first to get order_id
        order = orderRepository.save(order);
        orderRepository.flush(); // Force write to DB to get order_id
        System.out.println("Created order with ID: " + order.getOrderId());

        // Step 2: Set order reference in order items and save them
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            System.out.println(
                    "Setting order " + order.getOrderId() + " for orderItem product " + orderItem.getProductId());
        }
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        orderItemRepository.flush(); // Force write to DB
        System.out.println("Created " + savedOrderItems.size() + " order items for order " + order.getOrderId());

        // Step 3: Update order with order items list for JPA relationship
        order.setOrderItems(savedOrderItems);

        // Step 4: Update product quantities
        updateProductQuantities(productIds, quantities);

        return order;
    }

    /**
     * Create authenticated user order
     */
    public Order createUserOrder(
            List<Integer> productIds,
            List<Integer> quantities,
            int userId,
            Integer deliveryId,
            boolean isExpress,
            String customerEmail,
            String customerFullName,
            String customerPhone,
            String deliveryAddress,
            String deliveryProvince) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate products and calculate subtotal
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (int index = 0; index < productIds.size(); index++) {
            final int i = index; // Make effectively final
            Product product = productRepository.findById(productIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productIds.get(i)));

            int quantity = quantities.get(i);
            if (product.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getTitle());
            }

            BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));
            subtotalAmount = subtotalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.orderitemBuilder()
                    .productId(product.getId())
                    .quantity(quantity)
                    .price(product.getPrice()) // Use product price, not itemTotal
                    .build();
            orderItems.add(orderItem);
        }

        // Get shipping method
        ShippingMethod shippingMethod = getShippingMethod(isExpress);

        // Calculate shipping fees using ShippingMethod base fee + province adjustment
        String province = deliveryProvince != null ? deliveryProvince : "Default";
        BigDecimal shippingFees = calculateShippingFees(province, subtotalAmount, shippingMethod);
        BigDecimal totalAmount = subtotalAmount.add(shippingFees);

        // Create order
        Order order = Order.orderBuilder()
                .user(user)
                .transactionId(null)
                .shippingMethod(shippingMethod)
                .shippingFees(shippingFees)
                .totalAmount(totalAmount)
                .subtotalAmount(subtotalAmount)
                .createdAt(LocalDateTime.now())
                .isPayment(false)
                // Customer info from parameters
                .customerEmail(customerEmail)
                .customerFullName(customerFullName)
                .customerPhone(customerPhone)
                .deliveryAddress(deliveryAddress)
                .deliveryProvince(deliveryProvince)
                .build();

        // Step 1: Save order first to get order_id
        order = orderRepository.save(order);
        orderRepository.flush(); // Force write to DB to get order_id
        System.out.println("Created order with ID: " + order.getOrderId());

        // Step 2: Set order reference and save order items
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            System.out.println(
                    "Setting order " + order.getOrderId() + " for orderItem product " + orderItem.getProductId());
        }
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);
        orderItemRepository.flush(); // Force write to DB
        System.out.println("Created " + savedOrderItems.size() + " order items for order " + order.getOrderId());

        // Step 3: Update order with order items list for JPA relationship (no need to
        // save again due to cascade)
        order.setOrderItems(savedOrderItems);

        // Step 4: Update product quantities
        updateProductQuantities(productIds, quantities);

        return order;
    }

    private ShippingMethod getShippingMethod(boolean isExpress) {
        // Standard shipping: method_id = 1, fee = 22000
        // Express shipping: method_id = 2, fee = 30000
        int methodId = isExpress ? 2 : 1;
        double expectedFee = isExpress ? 30000.0 : 22000.0;
        String methodName = isExpress ? "Express 2H" : "Standard";

        return shippingMethodRepository.findById(methodId)
                .orElseGet(() -> {
                    // Create and save default shipping method if not exists
                    ShippingMethod defaultMethod = ShippingMethod.shippingMethodBuilder()
                            .methodName(methodName)
                            .isRush(isExpress)
                            .shippingFees(expectedFee)
                            .build();
                    System.out.println("🚛 Creating new shipping method: " + methodName + " with fee: " + expectedFee);
                    return shippingMethodRepository.save(defaultMethod);
                });
    }

    private BigDecimal calculateShippingFees(String province, BigDecimal subtotalAmount,
            ShippingMethod shippingMethod) {
        // Use base fee from ShippingMethod
        BigDecimal baseFee = BigDecimal.valueOf(shippingMethod.getShippingFees());

        System.out.println("=== Shipping Fees Calculation ===");
        System.out.println(
                "Shipping Method: " + shippingMethod.getMethodName() + " (ID: " + shippingMethod.getMethodID() + ")");
        System.out.println("Base Fee from ShippingMethod: " + baseFee);
        System.out.println("Province: " + province);

        // Mapping: shipping_fee = 22000 → method_id = 1, shipping_fee = 30000 →
        // method_id = 2
        BigDecimal finalFee;
        if (shippingMethod.getMethodID() == 1) { // Standard shipping
            // Standard base is 22000, but can be adjusted for provinces
            if (province.contains("HCM") || province.contains("Hà Nội")) {
                finalFee = BigDecimal.valueOf(22000); // 22000 for major cities
                System.out.println("Standard shipping - Major city: " + finalFee);
            } else {
                finalFee = BigDecimal.valueOf(30000); // 30000 for other provinces
                System.out.println("Standard shipping - Other province: " + finalFee);
            }
        } else { // Express shipping (method_id = 2)
            // Express is typically more expensive and fixed
            finalFee = BigDecimal.valueOf(30000); // Fixed 30000 for express
            System.out.println("Express shipping: " + finalFee);
        }

        System.out.println("Final shipping fee: " + finalFee);
        System.out.println("=================================");
        return finalFee;
    }

    private void updateProductQuantities(List<Integer> productIds, List<Integer> quantities) {
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productRepository.findById(productIds.get(i)).orElse(null);
            if (product != null) {
                product.setQuantity(product.getQuantity() - quantities.get(i));
                productRepository.save(product);
            }
        }
    }

    /**
     * Update order payment status and create transaction record
     * This method creates the transaction info when payment is successful
     */
    @Transactional
    public Order markOrderAsPaid(int orderId, String transactionId, String paymentMethod, String vnpResponseCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Create transaction information
        createTransactionForOrder(
                orderId,
                paymentMethod,
                "SUCCESS", // Payment successful
                "Order payment completed",
                null, // vnpBankCode - can be null
                null, // vnpBankTranNo - can be null
                vnpResponseCode,
                transactionId,
                "ORDER_" + orderId);

        return orderRepository.findById(orderId).orElse(order);
    }

    /**
     * Create transaction information for an order
     * This should be called after order and orderitems are created
     */
    public TransactionInformation createTransactionForOrder(
            int orderId,
            String paymentMethod,
            String status,
            String content,
            String vnpBankCode,
            String vnpBankTranNo,
            String vnpResponseCode,
            String vnpTransactionNo,
            String orderReference) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        TransactionInformation transaction = TransactionInformation.builder()
                .order(order) // Use the Order object instead of orderId
                .totalFee(order.getTotalAmount())
                .status(status)
                .transactionTime(LocalDateTime.now())
                .content(content)
                .paymentMethod(paymentMethod)
                .vnpBankCode(vnpBankCode)
                .vnpBankTranNo(vnpBankTranNo)
                .vnpResponseCode(vnpResponseCode)
                .vnpTransactionNo(vnpTransactionNo)
                .orderReference(orderReference)
                .build();
        TransactionInformation savedTransaction = transactionInformationRepository.save(transaction);

        // Update order with transaction reference
        order.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
        order.setPayment(true);
        orderRepository.save(order);
        System.out.println("✅ Order payment status updated and saved");

        return savedTransaction;
    }

    // Legacy methods for backward compatibility
    @Override
    public Order createRegularOrder(List<Integer> orderProductIds, int userId) {
        // Extract product IDs and quantities from OrderItems
        List<Integer> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (int orderItemId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("Order item not found"));
            productIds.add(orderItem.getProductId());
            quantities.add(orderItem.getQuantity());
        }

        return createUserOrder(productIds, quantities, userId, null, false,
                "user@example.com", "User", "", "Default Address", "Default Province");
    }

    @Override
    public Order createRegularOrderWithDeliveryId(List<Integer> orderProductIds, int deliveryId) {
        DeliveryInformation deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

        List<Integer> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (int orderItemId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("Order item not found"));
            productIds.add(orderItem.getProductId());
            quantities.add(orderItem.getQuantity());
        }

        return createUserOrder(productIds, quantities, deliveryInfo.getUser().getId(), deliveryId, false,
                deliveryInfo.getUser().getEmail(), deliveryInfo.getName(), deliveryInfo.getPhone(),
                deliveryInfo.getAddress(), deliveryInfo.getProvince());
    }

    @Override
    public Order createExpressOrder(List<Integer> orderProductIds, int deliveryId) {
        DeliveryInformation deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

        List<Integer> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (int orderItemId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new RuntimeException("Order item not found"));
            productIds.add(orderItem.getProductId());
            quantities.add(orderItem.getQuantity());
        }

        return createUserOrder(productIds, quantities, deliveryInfo.getUser().getId(), deliveryId, true,
                deliveryInfo.getUser().getEmail(), deliveryInfo.getName(), deliveryInfo.getPhone(),
                deliveryInfo.getAddress(), deliveryInfo.getProvince());
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(int userId) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getUser() != null && order.getUser().getId() == userId)
                .sorted(Comparator.comparing(Order::getOrderId).reversed())
                .toList();

        List<OrderDTO> ordersDto = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                    .filter(op -> op.getOrder().getOrderId() == order.getOrderId())
                    .toList();

            List<Product> products = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Optional<Product> optionalProduct = productRepository.findById(orderItem.getProductId());
                if (optionalProduct.isPresent()) {
                    Product p = optionalProduct.get();
                    p.setQuantity(orderItem.getQuantity());
                    products.add(p);
                }
            }

            OrderDTO orderDto = new OrderDTO(
                    order.getOrderId(),
                    order.getShippingFees(),
                    order.getTotalAmount(),
                    order.getCreatedAt(),
                    0, // VAT deprecated
                    order.getTotalAmount(), // Use totalAmount instead of totalFees
                    order.isPayment(),
                    null, // deliveryInformation no longer exists
                    products);
            ordersDto.add(orderDto);
        }
        return ordersDto;
    }

    @Override
    public Order updateOrderStatus(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPayment(!order.isPayment());
        return orderRepository.save(order);
    }
}
