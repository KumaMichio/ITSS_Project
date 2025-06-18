package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import models.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
