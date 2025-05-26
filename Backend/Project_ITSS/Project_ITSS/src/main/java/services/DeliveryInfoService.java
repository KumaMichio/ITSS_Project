package services;

import lombok.RequiredArgsConstructor;
import models.DeliveryInformation;
import org.springframework.stereotype.Service;
import repositories.DeliveryInfoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryInfoService implements IDeliveryInfoService{
    private final DeliveryInfoRepository deliveryInfoRepository;

    @Override
    public List<DeliveryInformation> getAllDeliveryInfos() {
        return deliveryInfoRepository.findAll();
    }

    @Override
    public Optional<DeliveryInformation> getDeliveryInfoById(int id) {
        return deliveryInfoRepository.findById(id);
    }

    @Override
    public DeliveryInformation addDeliveryInfo(DeliveryInformation deliveryInfo) {
        return deliveryInfoRepository.save(deliveryInfo);
    }

    @Override
    public DeliveryInformation updateDeliveryInfo(int id, DeliveryInformation deliveryInfo) {
        Optional<DeliveryInformation> existingDeliveryInfo = deliveryInfoRepository.findById(id);
        if (existingDeliveryInfo.isPresent()) {
            DeliveryInformation updatedDeliveryInfo = existingDeliveryInfo.get();
            updatedDeliveryInfo.setName(deliveryInfo.getName());
            updatedDeliveryInfo.setPhone(deliveryInfo.getPhone());
            updatedDeliveryInfo.setAddress(deliveryInfo.getAddress());
            updatedDeliveryInfo.setProvince(deliveryInfo.getProvince());
            updatedDeliveryInfo.setInstruction(deliveryInfo.getInstruction());
            updatedDeliveryInfo.setUser(deliveryInfo.getUser());
            return deliveryInfoRepository.save(updatedDeliveryInfo);
        }
        return null;
    }

    @Override
    public void deleteDeliveryInfo(int id) {
        deliveryInfoRepository.deleteById(id);
    }
    public Optional<DeliveryInformation> findByUserId(int userId) {
        return deliveryInfoRepository.findByUserId(userId);
    }

    @Override
    public List<DeliveryInformation> getAllDeliveryInfoByUserId(int userId) {
        return deliveryInfoRepository.findAll().stream()
                .filter(deliveryInfo -> deliveryInfo.getUser().getUserId() == userId)
                .toList();
    }

}
