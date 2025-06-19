package services;

import lombok.RequiredArgsConstructor;
import models.Order;
import models.TransactionInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.OrderRepository;
import repositories.TransactionInfoRepository;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService{
    private final OrderRepository orderRepository;

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

    @Override
    public void createTransaction(int orderId, String status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            double totalFee = order.getTotalFees(); // assuming totalFee is in the smallest currency unit

            TransactionInformation transactionInfo = TransactionInformation.builder()
                    .order(order)
                    .totalFee(totalFee)
                    .status(status)
                    .paymentMethod("VNPay")
                    .transactionTime(LocalDateTime.now())
                    .content("Payment for order " + orderId)
                    .build();

            transactionInfoRepository.save(transactionInfo);
        } else {
            // Handle the case where order is not found
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }
    }
    @Override
    public List<TransactionInformation> getTransactionsByOrderId(int orderId) {
        return transactionInfoRepository.findByOrder_OrderId(orderId);
    }

}
