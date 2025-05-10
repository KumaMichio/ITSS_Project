package repositories;

import models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTitleContaining(String title);

    List<Product> findByName(String name);

    Page<Product> findAll(Pageable pageable); // phân trang

}
