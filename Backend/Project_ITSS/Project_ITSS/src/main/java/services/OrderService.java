package services;

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

import java.util.List;

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
            OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            orderProduct.setOrderId(order.getId());
            orderProductRepository.save(orderProduct);
        }
        // Lưu đơn hàng
        return orderRepository.save(order);
    }
    @Override
    public Orders createRegularOrderWithDeliveryId(List<Integer> orderProductIds, int deliveryId) {
        // New implementation using deliveryId
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

        int userId = deliveryInfo.getUserId();

        int totalAmount = 0;
        double totalWeight = 0;
        for (int orderProductId : orderProductIds) {
            OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            Product product = productRepository.findById(orderProduct.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            totalAmount += orderProduct.getPrice();
            totalWeight += product.getWeight() * orderProduct.getQuantity();
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
        Orders order = Orders.orderBuilder()
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
            OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            orderProduct.setOrderId(order.getId());
            orderProductRepository.save(orderProduct);

        }
        // Lưu đơn hàng
        return orderRepository.save(order);
    }

    @Override
    public Orders createExpressOrder(List<Integer> orderProductIds, int deliveryId) {
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found"));

        if (!"Hà Nội".equalsIgnoreCase(deliveryInfo.getProvince())) {
            throw new RuntimeException("Express delivery is only available in Hà Nội");
        }

        int userId = deliveryInfo.getUserId();
        int totalAmount = 0;
        double totalWeight = 0;
        int expressShippingFee = 0;

        for (int orderProductId : orderProductIds) {
            OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            int productId = orderProduct.getProductId();
            if (productId < 1 || productId > 10) {
                throw new RuntimeException("Express delivery is only available for products with ID from 1 to 10");
            }
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            totalAmount += orderProduct.getPrice();
            totalWeight += product.getWeight() * orderProduct.getQuantity();
            expressShippingFee += 10000 * orderProduct.getQuantity();
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

        Orders order = Orders.orderBuilder()
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
            OrderProduct orderProduct = orderProductRepository.findById(orderProductId)
                    .orElseThrow(() -> new RuntimeException("Order product not found"));
            orderProduct.setOrderId(order.getId());
            orderProductRepository.save(orderProduct);

        }
        return orderRepository.save(order);
    }

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAll(); // Retrieve all orders from the repository
    }

    @Override
    public List<OrderDto> getOrdersByUserId(int userId) {
        List<Orders> orders = orderRepository.findAll().stream().filter((Orders order) -> order.getUserId() == userId).sorted(Comparator.comparing(Orders::getId).reversed()).toList();
        List<OrderDto> ordersDto = new ArrayList<OrderDto>();
        for (Orders order : orders) {
            Optional<DeliveryInfo> optionalDeliveryInfo = deliveryInfoRepository.findById(order.getDeliveryInfoId());
            DeliveryInfo deliveryInfo = optionalDeliveryInfo.get();
            //
            List<OrderProduct> orderProducts = orderProductRepository.findAll().stream().filter(op -> op.getOrderId() == order.getId()).toList();
            List<Product> products = new ArrayList<>();
            for (OrderProduct orderProduct : orderProducts) {
                Optional<Product> optionalProduct =  productRepository.findById(orderProduct.getProductId());
                Product p = optionalProduct.get();
                p.setQuantity(orderProduct.getQuantity());
                products.add(p);

            }

            OrderDto orderDto = new OrderDto(order.getId(), order.getShippingFees(), order.getTotalAmount(),order.getPlacedDate(), order.getCreatedAt(),order.getVAT(),order.getTotalFee(), order.getStartTime(), order.getEndTime(),order.isPayment(),deliveryInfo,products);
            ordersDto.add(orderDto);
        }
        return ordersDto;
    }

    @Override
    public Orders updateOrderStatus(int id) {
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setPayment(true);
        return orderRepository.save(order);
    }
}
