package services;

import lombok.RequiredArgsConstructor;
import models.ShippingMethod;
import org.springframework.stereotype.Service;
import repositories.ShippingMethodRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingMethodService implements IShippingMethodService {
    private final ShippingMethodRepository shippingMethodRepository;

    @Override
    public List<ShippingMethod> getShippingMethods() {
        return shippingMethodRepository.findAll();
    }
}
