package repositories;

import jakarta.transaction.Transactional;
import models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.awt.print.Pageable;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO products (title, price, category, imageUrl, quantity, entry_date, dimension, weight) Values (:title, :price, :category, :imageUrl, :quantity, :entryDate, :dimension, :weight, :sellerId)", nativeQuery = true)
    void CustomInsert(
            @Param("title") String title,
            @Param("price") double price,
            @Param("category") String category,
            @Param("imageUrl") String imageUrl,
            @Param("quantity") int quantity,
            @Param("entryDate") LocalDate entryDate,
            @Param("dimension") double dimension,
            @Param("weight") double weight
    );

    List<Product> findByTitleContaining(String title);

    List<Product> findByName(String name);

    // Page<Product> findAll(Pageable pageable); // phân trang

}
