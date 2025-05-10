package services;

import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import repositories.ProductRepository;

import java.util.List;

public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

}
