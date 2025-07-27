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
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final OrderRepository orderRepository;

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

    @Override
    public void createTransaction(int orderId, String status) {
        createTransaction(orderId, status, null, null, null, null);
    }

    @Override
    public void createTransaction(int orderId, String status, String vnpTransactionNo,
            String vnpBankCode, String vnpBankTranNo, String vnpResponseCode) {

        System.out.println("🔍 Creating transaction for order ID: " + orderId);
        System.out.println("📄 Transaction details:");
        System.out.println("  - Status: " + status);
        System.out.println("  - VNP Transaction No: " + vnpTransactionNo);
        System.out.println("  - VNP Bank Code: " + vnpBankCode);
        System.out.println("  - VNP Bank Tran No: " + vnpBankTranNo);
        System.out.println("  - VNP Response Code: " + vnpResponseCode);

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            double totalFee = order.getTotalAmount();

            System.out.println("✅ Order found - Total Fee: " + totalFee);

            TransactionInformation transactionInfo = TransactionInformation.builder()
                    .order(order)
                    .totalFee(totalFee)
                    .status(status)
                    .paymentMethod("VNPay")
                    .transactionTime(LocalDateTime.now())
                    .content("Payment for order " + orderId)
                    .vnpTransactionNo(vnpTransactionNo)
                    .vnpBankCode(vnpBankCode)
                    .vnpBankTranNo(vnpBankTranNo)
                    .vnpResponseCode(vnpResponseCode)
                    .build();

            try {
                TransactionInformation savedTransaction = transactionInfoRepository.save(transactionInfo);
                System.out.println("✅ Transaction saved successfully!");
                System.out.println("📝 Transaction ID: " + savedTransaction.getTransactionId());
                System.out.println("🏦 VNPay Transaction No: " + savedTransaction.getVnpTransactionNo());
                System.out.println("🏪 Order ID: " + savedTransaction.getOrder().getOrderId());
            } catch (Exception e) {
                System.err.println("❌ Error saving transaction to database: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to save transaction: " + e.getMessage(), e);
            }
        } else {
            // Handle the case where order is not found
            System.err.println("❌ Order not found with id: " + orderId);
            System.err.println("🔍 Available orders in database:");
            List<Order> allOrders = orderRepository.findAll();
            for (Order o : allOrders) {
                System.err.println("  - Order ID: " + o.getOrderId() + ", Total: " + o.getTotalAmount());
            }
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }
    }

    @Override
    public List<TransactionInformation> getTransactionsByOrderId(int orderId) {
        return transactionInfoRepository.findByOrder_OrderId(orderId);
    }

    @Override
    public List<TransactionInformation> getAllTransactions() {
        return transactionInfoRepository.findAll();
    }

    @Override
    public TransactionInformation getTransactionById(int id) {
        Optional<TransactionInformation> transaction = transactionInfoRepository.findById(id);
        return transaction.orElse(null);
    }

    @Override
    public boolean updateTransactionStatus(int id, String status) {
        try {
            Optional<TransactionInformation> transactionOpt = transactionInfoRepository.findById(id);
            if (transactionOpt.isPresent()) {
                TransactionInformation transaction = transactionOpt.get();
                transaction.setStatus(status);
                transactionInfoRepository.save(transaction);
                System.out.println("✅ Transaction status updated: " + id + " -> " + status);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error updating transaction status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Debug method để kiểm tra VNPay transactions
     */
    public List<TransactionInformation> getVNPayTransactions() {
        try {
            List<TransactionInformation> vnpayTransactions = transactionInfoRepository
                    .findByPaymentMethod("VNPay");

            System.out.println("🔍 Found " + vnpayTransactions.size() + " VNPay transactions:");
            for (TransactionInformation trans : vnpayTransactions) {
                System.out.println("  - Transaction ID: " + trans.getTransactionId());
                System.out.println("    Order ID: " + trans.getOrder().getOrderId());
                System.out.println("    VNP Transaction No: " + trans.getVnpTransactionNo());
                System.out.println("    Status: " + trans.getStatus());
                System.out.println("    Created: " + trans.getTransactionTime());
                System.out.println("    ----");
            }

            return vnpayTransactions;
        } catch (Exception e) {
            System.err.println("❌ Error fetching VNPay transactions: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
