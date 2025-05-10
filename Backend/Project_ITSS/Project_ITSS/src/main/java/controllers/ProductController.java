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

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }



    @GetMapping("")
    public ResponseEntity<String> getProducts(
            @RequestParam("page")   int page,
            @RequestParam("limit")  int limit
    ) {
        return ResponseEntity.ok("getProducts here");
    }


}
