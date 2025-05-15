package controllers;

import lombok.RequiredArgsConstructor;
import models.Book;
import models.CDLP;
import models.DVD;
import models.Product;
import org.springframework.web.bind.annotation.*;
import services.BookService;
import services.CDLPService;
import services.DVDService;
import services.ProductService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/products")
public class ProductController {

//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private DVDService dvdService;

    private final ProductService productService;
    private final DVDService dvdService;
    private final BookService bookService;
    private final CDLPService cdlpService;

    //--------------------------------------------------------------------------------------------------
    //Products

    @PostMapping("/add-product")
    public Product createProduct(@RequestBody Product product)
    {
//        Product savedProduct = productService.addProduct(product);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        return productService.insertProduct(product);
    }

    //get all product
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable int id){
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
    public Product updateProduct(@PathVariable int id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
    }

    //--------------------------------------------------------------------------------------
    // Books

    @PostMapping("/add-product/books")
    public void createProduct(@RequestBody Book book){
        bookService.addBook(book);
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/book/search/{title}")
    public List<Book> getBoookByTitleContaining(@PathVariable String title){
        return bookService.getBookByTitleContaining(title);
    }

    @GetMapping("/book/{id}")
    public Book getBook(@PathVariable int id) {
        return bookService.getBookById(id);
    }

    @PutMapping("/book/modify/{id}")
    public Book updateBook(@PathVariable int id, @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/book/delete/{id}")
    public void deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
    }

    @DeleteMapping("/book/delete")
    public void deleteAllBooks() {
        bookService.deleteAllBooks();
    }

    //---------------------------------------------------------------------------------------
    // CDLPs

    @PostMapping("/add-product/cdlp")
    public void addCDLPs(@RequestBody CDLP cdlp) {
        cdlpService.addCDLPs(cdlp);
    }

    @GetMapping("cdlp/search/{title}")
    public List<CDLP> getCDLPByTitleContaining(@PathVariable String title) {
        return cdlpService.getCDLPByTitleContaining(title);
    }

    @GetMapping("/cdlp")
    public List<CDLP> getAllCDLPs() {
        return cdlpService.getAllCDLPs();
    }

    @GetMapping("/cdlp/{id}")
    public CDLP getCDLPs(@PathVariable int id) {
        return cdlpService.getCDLP(id);
    }

    @PutMapping("/cdlp/modify/{id}")
    public CDLP updateCDLPs(@PathVariable int id, @RequestBody CDLP cdlpDetails) {
        return cdlpService.updateCDLP(id, cdlpDetails);
    }

    @DeleteMapping("/cdlp/delete/{id}")
    public void deleteCDLP(@PathVariable int id) {
        cdlpService.deleteCDLP(id);
    }

    @DeleteMapping("/cdlp/delete")
    public void deleteAllCDLPs() {
        cdlpService.deleteAllCDLPs();
    }

    //----------------------------------------------------------------------------------------------
    // DVDs

    @PostMapping("/add-product/dvd")
    public void createProduct(@RequestBody DVD dvd) {
        dvdService.addDVD(dvd);
    }

    @GetMapping("/dvd/search/{title}")
    public List<DVD> getDVDByTitleContaining(@PathVariable String title) {
        return dvdService.getDVDByTitleContaining(title);
    }

    @GetMapping("/dvd")
    public List<DVD> getAllDVDs() {
        return dvdService.getAllDVDs();
    }

    @GetMapping("/dvd/{id}")
    public DVD getDVD(@PathVariable int id) {
        return dvdService.getDVDById(id);
    }

    @PutMapping("/dvd/modify/{id}")
    public DVD updateDVD(@PathVariable int id, @RequestBody DVD dvdDetails) {
        return dvdService.updateDVD(id, dvdDetails);
    }

    @DeleteMapping("/dvd/delete/{id}")
    public void deleteDVD(@PathVariable int id) {
        dvdService.deleteDVD(id);
    }

}
