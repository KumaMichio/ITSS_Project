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
    @Query("UPDATE OrderProduct c SET c.orderId = :id WHERE c.id IN :orderProductIds")
    void updateUserIdByOrderProductIds(@Param("orderProductIds") List<Integer> orderProductIds, @Param("id") int id);
}
