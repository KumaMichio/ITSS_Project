package services;

import models.DeliveryInformation;

import java.util.List;
import java.util.Optional;

public interface IDeliveryInfoService {
    List<DeliveryInformation> getAllDeliveryInfos();
    Optional<DeliveryInformation> getDeliveryInfoById(int id);
    DeliveryInformation addDeliveryInfo(DeliveryInformation deliveryInfo);
    DeliveryInformation updateDeliveryInfo(int id, DeliveryInformation deliveryInfo);
    void deleteDeliveryInfo(int id);
    Optional<DeliveryInformation> findByUserId(int userId);

    List<DeliveryInformation> getAllDeliveryInfoByUserId(int userId);
}
