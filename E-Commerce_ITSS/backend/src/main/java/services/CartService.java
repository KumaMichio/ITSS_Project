package services;

import models.OrderItem;
import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.CartRepository;
import repositories.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }


    private OrderItem applyProductPrice(OrderItem orderItem, Product product) {
        double totalPrice = product.getPrice() * orderItem.getQuantity();
        orderItem.setPrice(totalPrice);
        return orderItem;
    }

    @Override
    public List<OrderItem> getAllOrderItems() {
        return cartRepository.findAll();
    }

    @Override
    public Optional<OrderItem> getOrderItemById(int id) {
        return cartRepository.findById(id);
    }

    @Override
    public OrderItem addOrderItem(OrderItem orderItem) {
//        Optional<Product> productOpt = productRepository.findById(orderProduct.getProductId());
//        if (productOpt.isPresent()) {
//            Product product = productOpt.get();
//            orderProduct.setPrice(product.getPrice() * orderProduct.getQuantity());
//            return cartRepository.save(orderProduct);
//        }
//        return null;
        Product product = productRepository.findById(orderItem.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return cartRepository.save(applyProductPrice(orderItem, product));
    }

    @Override
    public List<OrderItem> addOrderItems(List<OrderItem> orderItems) {
//        List<OrderItem> returningOrderItems = new ArrayList<>();
//        for (OrderItem orderItem : orderItems) {
//            Optional<Product> productOpt = productRepository.findById(orderItem.getProductId());
//            if (productOpt.isPresent()) {
//                Product product = productOpt.get();
//                orderItem.setPrice(product.getPrice() * orderItem.getQuantity());
//                OrderItem op =  cartRepository.save(orderItem);
//                returningOrderItems.add(op);
//            }
//        }
//        return returningOrderItems;
        return orderItems.stream()
                .map(op -> productRepository.findById(op.getProductId())
                        .map(product -> cartRepository.save(applyProductPrice(op, product)))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItem updateOrderItem(int id, OrderItem orderItem) {
//        Optional<OrderItem> existingOrderProductOpt = cartRepository.findById(id);
//        if (existingOrderProductOpt.isPresent()) {
//            OrderItem existingOrderProduct = existingOrderProductOpt.get();
//            Optional<Product> productOpt = productRepository.findById(orderItem.getProductId());
//            if (productOpt.isPresent()) {
//                Product product = productOpt.get();
//                existingOrderProduct.setQuantity(orderItem.getQuantity());
//                existingOrderProduct.setPrice(product.getPrice() * orderItem.getQuantity());
//                return cartRepository.save(existingOrderProduct);
//            }
//        }
//        return null;
        OrderItem existingOrderItem = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order product not found"));

        Product product = productRepository.findById(orderItem.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingOrderItem.setQuantity(orderItem.getQuantity());
        existingOrderItem.setPrice(product.getPrice() * orderItem.getQuantity());
        return cartRepository.save(existingOrderItem);
    }

    @Override
    public String updateOrderItemId(List<Integer> orderItemIds, int id) {
        cartRepository.updateUserIdByOrderProductIds(orderItemIds, id);
        return "Updated Order Product successfully";
    }

    @Override
    public void deleteOrderItem(int id) {
        cartRepository.deleteById(id);
    }
}
