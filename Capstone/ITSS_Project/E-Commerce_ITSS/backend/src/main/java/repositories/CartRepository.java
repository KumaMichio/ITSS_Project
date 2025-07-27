package repositories;

import jakarta.transaction.Transactional;
import models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<OrderItem, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE OrderItem c SET c.order = (SELECT o FROM Order o WHERE o.id = :id) WHERE c.id IN :orderItemIds")
    void updateUserIdByOrderProductIds(@Param("orderItemIds") List<Integer> orderItemIds, @Param("id") int id);
}
