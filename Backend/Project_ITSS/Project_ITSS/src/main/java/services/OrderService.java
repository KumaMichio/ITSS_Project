package services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repositories.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
}
