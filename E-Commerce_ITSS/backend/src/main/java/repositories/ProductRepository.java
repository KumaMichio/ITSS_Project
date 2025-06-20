package repositories;

import jakarta.transaction.Transactional;
import models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO products (title, price, category, imageURL, quantity, entry_date, dimension, weight, user_id)
            VALUES (:title, :price, :category, :imageUrl, :quantity, :entryDate, :dimension, :weight, :sellerId)
            """, nativeQuery = true)
    void customInsert(
            @Param("title") String title,
            @Param("price") double price,
            @Param("category") String category,
            @Param("imageUrl") String imageUrl,
            @Param("quantity") int quantity,
            @Param("entryDate") LocalDate entryDate,
            @Param("dimension") double dimension,
            @Param("weight") double weight,
            @Param("sellerId") int sellerId);

    List<Product> findByTitleContaining(String title);

    List<Product> findByCategory(String category);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL")
    List<String> findDistinctCategories();

    // Page<Product> findAll(Pageable pageable);

    List<Product> findBySellerId_Id(int Id);
}
