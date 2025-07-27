package services;

import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.Book;
import models.CDLP;
import models.DVD;
import models.Product;
import org.springframework.stereotype.Service;
import repositories.ProductRepository;
import repositories.DVDRepository;
import repositories.BookRepository;
import repositories.CDLPRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {
    // @Autowired
    // private ProductRepository productRepository;
    //
    // @Autowired
    // private DVDRepository dvdRepository;
    //
    // @Autowired
    // private BookRepository bookRepository;
    //
    // @Autowired
    // private CDLPRepository cdlpRepository;

    private final ProductRepository productRepository;
    private final DVDRepository dvdRepository;
    private final BookRepository bookRepository;
    private final CDLPRepository cdlpRepository;

    public void addProduct(Product product) {
        productRepository.customInsert(
                product.getTitle(),
                product.getPrice(),
                product.getCategory(),
                product.getImageURL(),
                product.getQuantity(),
                product.getEntry_date(),
                product.getDimension(),
                product.getWeight(),
                product.getSellerId().getId());
    }

    public Product insertProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getProductByTitleContaining(String title) {
        return productRepository.findByTitleContaining(title);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    public List<Product> getRandomProducts(int limit) {
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.size() <= limit) {
            return allProducts;
        }

        // Shuffle the list and take first 'limit' items
        java.util.Collections.shuffle(allProducts);
        return allProducts.subList(0, limit);
    }

    public Product updateProduct(Integer id, Product product) {
        Product products = productRepository.findById(id).orElse(null);
        if (products != null) {
            products.setTitle(product.getTitle());
            products.setPrice(product.getPrice());
            products.setCategory(product.getCategory());
            products.setImageURL(product.getImageURL());
            products.setQuantity(product.getQuantity());
            products.setEntry_date(product.getEntry_date());
            products.setDimension(product.getDimension());
            products.setWeight(product.getWeight());
            return productRepository.save(products);
        } else {
            throw new ResourceNotFoundException("product not found" + id);
        }
    }

    public void deleteProduct(Integer id) {
        DVD dvd = dvdRepository.findById(id).orElse(null);
        Book book = bookRepository.findById(id).orElse(null);
        CDLP cdlp = cdlpRepository.findById(id).orElse(null);
        String category = productRepository.findById(id).get().getCategory();
        System.out.println(category);
        if (category.equalsIgnoreCase("dvd")) {
            if (dvd == null) {
                productRepository.deleteById(id);
            } else {
                dvdRepository.deleteById(id);
                productRepository.deleteById(id);
            }
        }

        if (category.equalsIgnoreCase("book")) {
            if (book == null) {
                productRepository.deleteById(id);
            } else {
                bookRepository.deleteById(id);
                productRepository.deleteById(id);
            }
        }

        if (category.equalsIgnoreCase("cdlp")) {
            if (cdlp == null) {
                productRepository.deleteById(id);
            } else {
                cdlpRepository.deleteById(id);
                productRepository.deleteById(id);
            }
        }

    }

}
