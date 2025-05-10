package controllers;

import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ProductService;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    //--------------------------------------------------------------------------------------------------
    //Products

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.addProduct(Product product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    //get all product
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable long id){
        return productService.getProductById(id);
    }

    //get product by title containing
    @GetMapping("/search/{title}")
    public List<Product> getProductByTitleContaining(@PathVariable String title) {
        return productService.getProductByTitleContaining(title);
    }


//    @GetMapping("")
//    public ResponseEntity<String> getProducts(
//            @RequestParam("page")   int page,
//            @RequestParam("limit")  int limit
//    ) {
//        return ResponseEntity.ok("getProducts here");
//    }

    @PutMapping("/modify/{id}")
    public Product updateProduct(@PathVariable long id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
    }


}
