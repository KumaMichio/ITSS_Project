package services;

import models.Product;
import models.OrderItem;
import repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class StockValidationService {

    private final ProductRepository productRepository;

    /**
     * Validate stock for a single product
     */
    public boolean validateStock(Product product, int requestedQuantity) {
        return product.getQuantity() >= requestedQuantity;
    }

    /**
     * Validate stock for multiple products
     */
    public StockValidationResult validateCartStock(List<Integer> productIds, List<Integer> quantities) {
        List<String> errors = new ArrayList<>();
        Map<Integer, Integer> availableStock = new HashMap<>();
        boolean isValid = true;

        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int requestedQuantity = quantities.get(i);

            Product product = productRepository.findById(productId).orElse(null);

            if (product == null) {
                errors.add("Sản phẩm với ID " + productId + " không tồn tại");
                isValid = false;
                continue;
            }

            availableStock.put(productId, product.getQuantity());

            if (product.getQuantity() < requestedQuantity) {
                errors.add("Sản phẩm '" + product.getTitle() + "' không đủ số lượng. " +
                        "Có sẵn: " + product.getQuantity() + ", Yêu cầu: " + requestedQuantity);
                isValid = false;
            }
        }

        return new StockValidationResult(isValid, errors, availableStock);
    }

    /**
     * Check and update stock when creating order
     */
    public void reserveStock(List<Integer> productIds, List<Integer> quantities) {
        for (int i = 0; i < productIds.size(); i++) {
            int productId = productIds.get(i);
            int quantity = quantities.get(i);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            if (product.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getTitle());
            }

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
        }
    }

    /**
     * Restore stock when order is cancelled
     */
    public void restoreStock(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }

    /**
     * Get current stock for a product
     */
    public int getCurrentStock(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        return product.getQuantity();
    }

    /**
     * Stock validation result
     */
    public static class StockValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final Map<Integer, Integer> availableStock;

        public StockValidationResult(boolean valid, List<String> errors, Map<Integer, Integer> availableStock) {
            this.valid = valid;
            this.errors = errors;
            this.availableStock = availableStock;
        }

        // Getters
        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public Map<Integer, Integer> getAvailableStock() {
            return availableStock;
        }
    }
}
