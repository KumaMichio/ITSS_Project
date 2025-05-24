package services;

import dtos.OrderDTO;
import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import models.DeliveryInformation;
import models.Product;
import org.springframework.stereotype.Service;
import repositories.DeliveryInfoRepository;
import repositories.OrderItemRepository;
import repositories.OrderRepository;
import models.OrderItem;
import models.Order;
import repositories.ProductRepository;

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

    // ----------------- Utility methods -------------------------

    @Override
    public Order createRegularOrder(List<Integer> orderProductIds, int userId) {
        // Lấy thông tin giao hàng của người dùng
        DeliveryInformation deliveryInfo = deliveryInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

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

        // Tạo đơn hàng mới
        Order order = Order.orderBuilder()
                .shippingMethodId(shippingFees)
                .deliveryInfoId(deliveryInfo.getId())
                .totalAmount(totalAmount)
                .userId(userId)
                .placedDate(LocalDate.now())
                .createdAt(LocalTime.now())
                .shippingMethodId(1) // Giao hàng thường
                .VAT(vat)
                .totalFee(totalFee)
                .build();
        for (int orderProductId : orderProductIds)
        {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            orderItem.setOrderId(order.getOrder_id());
            orderItemRepository.save(orderItem);
        }
        // Lưu đơn hàng
        return orderRepository.save(order);
    }
    @Override
    public Order createRegularOrderWithDeliveryId(List<Integer> orderProductIds, int deliveryId) {
        // New implementation using deliveryId
        DeliveryInformation deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

        int userId = deliveryInfo.getUserId();

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

        // Tạo đơn hàng mới
        Order order = Order.orderBuilder()
                .shippingMethodId(shippingFees)
                .deliveryInfoId(deliveryInfo.getId())
                .totalAmount(totalAmount)
                .userId(userId)
                .placedDate(LocalDate.now())
                .createdAt(LocalTime.now())
                .shippingMethodId(1) // Giao hàng thường
                .VAT(vat)
                .totalFee(totalFee)
                .build();
        for (int orderProductId : orderProductIds)
        {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            orderItem.setOrderId(order.getOrder_id());
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

        int userId = deliveryInfo.getUserId();
        int totalAmount = 0;
        double totalWeight = 0;
        int expressShippingFee = 0;

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
        }

        int shippingFees = 25000; // base fee for Hanoi
        if (totalWeight > 3) {
            shippingFees += (int) (2500 * Math.ceil((totalWeight - 3) / 0.5));
        }
        shippingFees += expressShippingFee;
        if (totalAmount > 100000) {
            shippingFees -= 25000;
        }

        int vat = (int) (totalAmount / 10); // VAT is 10% of total amount
        int totalFee = totalAmount + shippingFees + vat;

        Order order = Order.orderBuilder()
                .shippingFees(shippingFees)
                .deliveryInfoId(deliveryInfo.getId())
                .totalAmount(totalAmount)
                .userId(userId)
                .placedDate(LocalDate.now())
                .createdAt(LocalTime.now())
                .shippingMethodId(2) // Express delivery
                .VAT(vat)
                .totalFee(totalFee)
                .build();
        for (int orderProductId : orderProductIds)
        {
            OrderItem orderItem = orderItemRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            orderItem.setOrderId(order.getOrder_id());
            orderItemRepository.save(orderItem);

        }
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll(); // Retrieve all orders from the repository
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(int userId) {
        List<Order> orders = orderRepository.findAll().stream().filter((Order order) -> order.getUserID().getUser_id() == userId).sorted(Comparator.comparing(Order::getOrder_id).reversed()).toList();
        List<OrderDTO> ordersDto = new ArrayList<OrderDTO>();
        for (Order order : orders) {
            Optional<DeliveryInformation> optionalDeliveryInfo = deliveryInfoRepository.findById(order.getDeliveryID());
            DeliveryInformation deliveryInfo = optionalDeliveryInfo.get();
            //
            List<OrderItem> orderItems = orderItemRepository.findAll().stream().filter(op -> op.getOrderId() == order.getOrder_id()).toList();
            List<Product> products = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                Optional<Product> optionalProduct =  productRepository.findById(orderItem.getProductId());
                Product p = optionalProduct.get();
                p.setQuantity(orderItem.getQuantity());
                products.add(p);

            }

            OrderDTO orderDto = new OrderDTO(order.getOrder_id(), order.getShippingFees(), order.getTotalAmount(),order.getPlacedDate(), order.getCreatedAt(),order.getVAT(),order.getTotalFee(), order.getStartTime(), order.getEndTime(),order.isPayment(),deliveryInfo,products);
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