package services;

import exceptions.ResourceNotFoundException;
import models.DVD;
import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import repositories.ProductRepository;
import repositories.DVDRepository;

import java.util.List;

public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DVDRepository dvdRepository;

    public void addProduct(Product product) {
        productRepository.CustomInsert(
                product.getTitle(),
                product.getPrice(),
                product.getCategory(),
//                product.getImageUrl(),
                product.getQuantity(),
                product.getEntry_date(),
                product.getDimension(),
                product.getWeight()
        );
    }


    public Product insertProduct(Product product) {
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

    public List<Product> getProductByTitleContaining(String title){
        return productRepository.findByTitleContaining(title);
    }

    public Product updateProduct(Long id, Product product){
        Product products = productRepository.findById(id).orElse(null);
        if (products != null) {
            products.setTitle(product.getTitle());
            products.setPrice(product.getPrice());
            products.setCategory(product.getCategory());
//            products.setImageUrl(product.getImageUrl());
            products.setQuantity(product.getQuantity());
            products.setEntry_date(product.getEntry_date());
            products.setDimension(product.getDimension());
            products.setWeight(product.getWeight());
            return productRepository.save(products);
        }
        else {
            throw new ResourceNotFoundException("product not found" + id);
        }
    }

    public void deleteProduct(Long id) {
        DVD dvds = dvdRepository.findById(id).orElse(null);
        Books book = BookRepository.findById(id).orElse(null);
        CDLPs cdlps = CDLPRepository.findById(id).orElse(null);
        String category = productRepository.findById(id).get().getCategory();
        System.out.println(category);
        if (category.equalsIgnoreCase("dvds")) {
            if (dvds == null) {
                productRepository.deleteById(id);
            } else {
                dvdrepository.deleteById(id);
                productRepository.deleteById(id);
            }
        }

        if (category.equalsIgnoreCase("books")) {
            if (book == null) {
                productRepository.deleteById(id);
            }
            else {
                bookrepository.deleteById(id);
                productRepository.deleteById(id);
            }
        }

        if (category.equalsIgnoreCase("cdlps")) {
            if (cdlps == null) {
                productRepository.deleteById(id);
            }
            else {
                cdlprepository.deleteById(id);
                productRepository.deleteById(id);
            }
        }

    }


}
