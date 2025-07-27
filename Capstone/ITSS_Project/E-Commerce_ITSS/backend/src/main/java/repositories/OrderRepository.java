package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import models.Order;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Find all orders sorted by creation date (newest first)
    List<Order> findAllByOrderByCreatedAtDesc();

}
