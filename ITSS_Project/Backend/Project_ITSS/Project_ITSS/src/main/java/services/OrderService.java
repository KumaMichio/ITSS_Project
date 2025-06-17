package services;

import dtos.OrderDTO;
import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import models.*;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import repositories.*;

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
        int totalAmount = 0;
        double totalWeight = 0;
        for (int orderProductId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            totalAmount += orderItem.getPrice();
            totalWeight += product.getWeight() * orderItem.getQuantity();
        }

        int shippingFees;
        if (deliveryInfo.getProvince().equalsIgnoreCase("Hà Nội") || deliveryInfo.getProvince().equalsIgnoreCase("Ho Chi Minh city")) {
            if(totalWeight > 3)
            {
                shippingFees = (int) (2500 * Math.ceil((totalWeight-3) / 0.5)+25000);
            }
            else shippingFees = 25000;
        } else {
            if (totalWeight > 0.5)
            {
                shippingFees = (int) (2500 * Math.ceil((totalWeight-0.5) / 0.5)+30000);
            }else shippingFees = 30000;
        }
        if (totalAmount >100000 ) shippingFees -=25000;

        // Tính toán VAT và tổng phí
        int vat = (int) (totalAmount / 10); // VAT là 10% của tổng giá trị đơn hàng
        int totalFee = totalAmount + shippingFees + vat;

        // Tạo đơn hàng
        Order order = Order.orderBuilder()
                .user(user)
                .deliveryInformation(deliveryInfo)
                .shippingMethod(shippingMethod)
                .shippingFees(shippingFees)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .VAT(vat)
                .totalFees(totalFee)
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
        int totalAmount = 0;
        double totalWeight = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (int orderProductId : orderProductIds) {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            Product product = productRepository.findById(orderItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            totalAmount += orderItem.getPrice();
            totalWeight += product.getWeight() * orderItem.getQuantity();

            orderItems.add(orderItem); // Lưu lại danh sách orderItems để gán về sau
        }

        // Tính phí vận chuyển
        int shippingFees;
        if (deliveryInfo.getProvince().equalsIgnoreCase("Hà Nội") || deliveryInfo.getProvince().equalsIgnoreCase("Ho Chi Minh city")) {
            if(totalWeight > 3)
            {
                shippingFees = (int) (2500 * Math.ceil((totalWeight-3) / 0.5)+25000);
            }
            else shippingFees = 25000;
        } else {
            if (totalWeight > 0.5)
            {
                shippingFees = (int) (2500 * Math.ceil((totalWeight-0.5) / 0.5)+30000);
            }else shippingFees = 30000;
        }
        if (totalAmount >100000 ) shippingFees -=25000;

        // Tính toán VAT và tổng phí
        int vat = totalAmount / 10; // VAT là 10% của tổng giá trị đơn hàng
        int totalFee = totalAmount + shippingFees + vat;

        // Tạo đơn hàng mới
        Order order = Order.orderBuilder()
                .user(user)
                .deliveryInformation(deliveryInfo)
                .shippingMethod(shippingMethod)
                .shippingFees(shippingFees)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .VAT(vat)
                .totalFees(totalFee)
                .build();
        for (int orderProductId : orderProductIds)
        {
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
        int totalAmount = 0;
        double totalWeight = 0;
        int expressShippingFee = 0;
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

            totalAmount += orderItem.getPrice();
            totalWeight += product.getWeight() * orderItem.getQuantity();
            expressShippingFee += 10000 * orderItem.getQuantity();
            orderItems.add(orderItem);
        }

        double shippingFees = 25000; // base fee for Hanoi
        if (totalWeight > 3) {
            shippingFees += (int) (2500 * Math.ceil((totalWeight - 3) / 0.5));
        }
        shippingFees += expressShippingFee;
        if (totalAmount > 100000) {
            shippingFees -= 25000;
        }

        int vat = (int) (totalAmount / 10); // VAT is 10% of total amount
        double totalFees = totalAmount + shippingFees + vat;

        Order order = Order.orderBuilder()
                .user(user)
                .deliveryInformation(deliveryInfo)
                .shippingMethod(shippingMethodRepository.findById(2).orElseThrow(() -> new RuntimeException("Shipping method not found")))
                .shippingFees(shippingFees)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .VAT(vat)
                .totalFees(totalFees)
                .build();
        // Gán quan hệ hai chiều cho OrderItem
        for (OrderItem item : orderItems) {
            item.setOrder(order); // set Order cho từng item
        }

        order.setOrderItems(orderItems);

        return orderRepository.save(order);

//        for (int orderProductId : orderProductIds)
//        {
//            OrderItem orderItem = orderItemRepository.findById(orderProductId)
//                    .orElseThrow(() -> new RuntimeException("Order product not found"));
//            orderItem.setOrderId(order.getOrder_id());
//            orderItemRepository.save(orderItem);
//
//        }
//        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // Retrieve all orders from the repository
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(int userId) {
        List<Order> orders = orderRepository.findAll().stream().filter((Order order) -> order.getUser().getUserId() == userId).sorted(Comparator.comparing(Order::getOrderId).reversed()).toList();
        List<OrderDTO> ordersDto = new ArrayList<OrderDTO>();
        for (Order order : orders) {
            Optional<DeliveryInformation> optionalDeliveryInfo = deliveryInfoRepository.findById(order.getDeliveryInformation().getId());
            DeliveryInformation deliveryInfo = optionalDeliveryInfo.get();
            //
            List<OrderItem> orderItems = orderItemRepository.findAll().stream().filter(op -> op.getOrder().getOrderId() == order.getOrderId()).toList();
            List<Product> products = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Optional<Product> optionalProduct =  productRepository.findById(orderItem.getProductId());
                Product p = optionalProduct.get();
                p.setQuantity(orderItem.getQuantity());
                products.add(p);

            }
            OrderDTO orderDto = new OrderDTO(
                    order.getOrderId(),
                    order.getShippingFees(),
                    order.getTotalAmount(),
                    order.getCreatedAt(),
                    order.getVAT(),
                    order.getTotalFees(),
                    order.isPayment(),
                    order.getDeliveryInformation(),
                    products
            );
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