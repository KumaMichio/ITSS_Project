package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import models.Order;
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
