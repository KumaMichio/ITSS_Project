package repositories;

import models.TransactionInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionInfoRepository extends JpaRepository<TransactionInformation, Integer> {
    List<TransactionInformation> findByOrder_OrderId(int orderId);
}
