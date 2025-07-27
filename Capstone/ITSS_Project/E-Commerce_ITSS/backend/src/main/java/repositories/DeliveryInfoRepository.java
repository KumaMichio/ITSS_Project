package repositories;

import models.DeliveryInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DeliveryInfoRepository extends JpaRepository<DeliveryInformation, Integer> {
    Optional<DeliveryInformation> findByUserId(int userId);
}
