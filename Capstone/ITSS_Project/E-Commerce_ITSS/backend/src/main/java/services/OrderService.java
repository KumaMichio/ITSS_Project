package services;

import dtos.OrderDTO;
import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import models.*;
import org.springframework.stereotype.Service;
import repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ShippingMethodRepository shippingMethodRepository;

    // ----------------- Utility methods -------------------------

    @Override
    public Order createRegularOrder(List<Integer> orderProductIds, int userId) {
        // Lấy thông tin các đối tượng liên quan
        DeliveryInformation deliveryInfo = deliveryInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ShippingMethod shippingMethod = shippingMethodRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Shipping method not found")); // Giao hàng thường

        // Tính toán giá trị đơn hàng và phí vận chuyển
        BigDecimal totalAmount = BigDecimal.ZERO;
        double totalWeight = 0;
        for (int orderProductId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            totalAmount = totalAmount.add(BigDecimal.valueOf(orderItem.getPrice()));
            totalWeight += product.getWeight() * orderItem.getQuantity();
        }

        BigDecimal shippingFees;
        if (deliveryInfo.getProvince().equalsIgnoreCase("Hà Nội")) {
            if (totalWeight > 3) {
                double extraFee = 2500 * Math.ceil((totalWeight - 3) / 0.5) + 25000;
                shippingFees = BigDecimal.valueOf(extraFee);
            } else
                shippingFees = BigDecimal.valueOf(25000);
        } else {
            if (totalWeight > 0.5) {
                double extraFee = 2500 * Math.ceil((totalWeight - 0.5) / 0.5) + 30000;
                shippingFees = BigDecimal.valueOf(extraFee);
            } else
                shippingFees = BigDecimal.valueOf(30000);
        }
        if (totalAmount.compareTo(BigDecimal.valueOf(100000)) > 0)
            shippingFees = shippingFees.subtract(BigDecimal.valueOf(25000));

        // Tính toán VAT và tổng phí
        BigDecimal vat = totalAmount.divide(BigDecimal.valueOf(10)); // VAT là 10% của tổng giá trị đơn hàng
        BigDecimal totalFee = totalAmount.add(shippingFees).add(vat);

        // Tạo đơn hàng
        Order order = Order.orderBuilder()
                .user(user)
                .shippingMethod(shippingMethod)
                .shippingFees(shippingFees)
                .totalAmount(totalFee) // Use totalFee instead of totalAmount
                .createdAt(LocalDateTime.now())
                .customerEmail(user.getEmail())
                .customerFullName(user.getUsername()) // Use username as fullname
                .customerPhone(deliveryInfo.getPhone())
                .deliveryAddress(deliveryInfo.getAddress())
                .deliveryProvince(deliveryInfo.getProvince())
                .subtotalAmount(totalAmount)
                .build();

        // Gán order cho từng OrderItem
        for (int orderProductId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));

            orderItem.setOrder(order); // Gán quan hệ thay vì setOrderId
            order.getOrderItems().add(orderItem); // Thêm vào danh sách của đơn hàng (nếu bidirectional)
        }

        // Lưu đơn hàng và cascade lưu luôn OrderItems nếu có CascadeType.ALL
        return orderRepository.save(order);
    }

    @Override
    public Order createRegularOrderWithDeliveryId(List<Integer> orderProductIds, int deliveryId) {
        // Lấy thông tin địa chỉ giao hàng
        DeliveryInformation deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

        // Lấy user từ deliveryInfo
        User user = deliveryInfo.getUser();

        // sử dụng phương thức giao hàng thường
        ShippingMethod shippingMethod = shippingMethodRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));

        // Tính tổng tiền và khối lượng
        BigDecimal totalAmount = BigDecimal.ZERO;
        double totalWeight = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (int orderProductId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            totalAmount = totalAmount.add(BigDecimal.valueOf(orderItem.getPrice()));
            totalWeight += product.getWeight() * orderItem.getQuantity();

            orderItems.add(orderItem); // Lưu lại danh sách orderItems để gán về sau
        }

        // Tính phí vận chuyển
        BigDecimal shippingFees;
        if (deliveryInfo.getProvince().equalsIgnoreCase("Hà Nội")
                || deliveryInfo.getProvince().equalsIgnoreCase("Ho Chi Minh city")) {
            if (totalWeight > 3) {
                double extraFee = 2500 * Math.ceil((totalWeight - 3) / 0.5) + 25000;
                shippingFees = BigDecimal.valueOf(extraFee);
            } else
                shippingFees = BigDecimal.valueOf(25000);
        } else {
            if (totalWeight > 0.5) {
                double extraFee = 2500 * Math.ceil((totalWeight - 0.5) / 0.5) + 30000;
                shippingFees = BigDecimal.valueOf(extraFee);
            } else
                shippingFees = BigDecimal.valueOf(30000);
        }
        if (totalAmount.compareTo(BigDecimal.valueOf(100000)) > 0)
            shippingFees = shippingFees.subtract(BigDecimal.valueOf(25000));

        // Tính toán VAT và tổng phí
        BigDecimal vat = totalAmount.divide(BigDecimal.valueOf(10)); // VAT là 10% của tổng giá trị đơn hàng
        BigDecimal totalFee = totalAmount.add(shippingFees).add(vat);

        // Tạo đơn hàng mới
        Order order = Order.orderBuilder()
                .user(user)
                .shippingMethod(shippingMethod)
                .shippingFees(shippingFees)
                .totalAmount(totalFee)
                .createdAt(LocalDateTime.now())
                .customerEmail(user.getEmail())
                .customerFullName(user.getUsername())
                .customerPhone(deliveryInfo.getPhone())
                .deliveryAddress(deliveryInfo.getAddress())
                .deliveryProvince(deliveryInfo.getProvince())
                .subtotalAmount(totalAmount)
                .build();
        for (int orderProductId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);

        }
        // Lưu đơn hàng
        return orderRepository.save(order);
    }

    @Override
    public Order createExpressOrder(List<Integer> orderProductIds, int deliveryId) {
        DeliveryInformation deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

        if (!"Hà Nội".equalsIgnoreCase(deliveryInfo.getProvince())) {
            throw new RuntimeException("Express delivery is only available in Hà Nội");
        }

        User user = deliveryInfo.getUser();
        BigDecimal totalAmount = BigDecimal.ZERO;
        double totalWeight = 0;
        BigDecimal expressShippingFee = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (int orderProductId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));

            int productId = orderItem.getProductId();
            if (productId < 1 || productId > 10) {
                throw new RuntimeException("Express delivery is only available for products with ID from 1 to 10");
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            totalAmount = totalAmount.add(BigDecimal.valueOf(orderItem.getPrice()));
            totalWeight += product.getWeight() * orderItem.getQuantity();
            expressShippingFee = expressShippingFee
                    .add(BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            orderItems.add(orderItem);
        }

        BigDecimal shippingFees = BigDecimal.valueOf(25000); // base fee for Hanoi
        if (totalWeight > 3) {
            double extraFee = 2500 * Math.ceil((totalWeight - 3) / 0.5);
            shippingFees = shippingFees.add(BigDecimal.valueOf(extraFee));
        }
        shippingFees = shippingFees.add(expressShippingFee);
        if (totalAmount.compareTo(BigDecimal.valueOf(100000)) > 0) {
            shippingFees = shippingFees.subtract(BigDecimal.valueOf(25000));
        }

        BigDecimal vat = totalAmount.divide(BigDecimal.valueOf(10)); // VAT is 10% of total amount
        BigDecimal totalFees = totalAmount.add(shippingFees).add(vat);

        Order order = Order.orderBuilder()
                .user(user)
                .shippingMethod(shippingMethodRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Shipping method not found")))
                .shippingFees(shippingFees)
                .totalAmount(totalFees) // Use totalFees as final amount
                .createdAt(LocalDateTime.now())
                .customerEmail(user.getEmail())
                .customerFullName(user.getUsername())
                .customerPhone(deliveryInfo.getPhone())
                .deliveryAddress(deliveryInfo.getAddress())
                .deliveryProvince(deliveryInfo.getProvince())
                .subtotalAmount(totalAmount)
                .build();
        // Gán quan hệ hai chiều cho OrderItem
        for (OrderItem item : orderItems) {
            item.setOrder(order); // set Order cho từng item
        }

        order.setOrderItems(orderItems);

        return orderRepository.save(order);

        // for (int orderProductId : orderProductIds)
        // {
        // OrderItem orderItem = orderItemRepository.findById(orderProductId)
        // .orElseThrow(() -> new RuntimeException("Order product not found"));
        // orderItem.setOrderId(order.getOrder_id());
        // orderItemRepository.save(orderItem);
        //
        // }
        // return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // Retrieve all orders from the repository
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(int userId) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter((Order order) -> order.getUser().getId() == userId)
                .sorted(Comparator.comparing(Order::getOrderId).reversed()).toList();
        List<OrderDTO> ordersDto = new ArrayList<OrderDTO>();
        for (Order order : orders) {
            // deliveryInformation no longer exists, skip delivery lookup

            List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                    .filter(op -> op.getOrder().getOrderId() == order.getOrderId()).toList();
            List<Product> products = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Optional<Product> optionalProduct = productRepository.findById(orderItem.getProductId());
                Product p = optionalProduct.get();
                p.setQuantity(orderItem.getQuantity());
                products.add(p);

            }
            OrderDTO orderDto = new OrderDTO(
                    order.getOrderId(),
                    order.getShippingFees(),
                    order.getTotalAmount(),
                    order.getCreatedAt(),
                    0, // VAT is deprecated, use 0
                    order.getTotalAmount(),
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
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setPayment(true);
        return orderRepository.save(order);
    }
}