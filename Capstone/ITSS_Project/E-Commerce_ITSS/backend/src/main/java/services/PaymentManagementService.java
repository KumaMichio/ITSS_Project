package services;

import lombok.RequiredArgsConstructor;
import models.Order;
import models.TransactionInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.OrderRepository;
import repositories.TransactionInformationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentManagementService {

    private final TransactionInformationRepository transactionRepository;
    private final OrderRepository orderRepository;

    /**
     * Get all payment transactions with pagination
     */
    public Page<TransactionInformation> getAllPayments(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return transactionRepository.findAll(pageable);
    }

    /**
     * Search payments by various criteria
     */
    public Page<TransactionInformation> searchPayments(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionTime").descending());
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return transactionRepository.findAll(pageable);
        }
        return transactionRepository.searchTransactions(searchTerm.trim(), pageable);
    }

    /**
     * Get payments by status
     */
    public Page<TransactionInformation> getPaymentsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionTime").descending());
        return transactionRepository.findByStatus(status, pageable);
    }

    /**
     * Get payment details by transaction ID
     */
    public Optional<TransactionInformation> getPaymentById(int transactionId) {
        return transactionRepository.findById(transactionId);
    }

    /**
     * Get payment by order ID
     */
    public Optional<TransactionInformation> getPaymentByOrderId(int orderId) {
        return transactionRepository.findByOrderId(orderId);
    }

    /**
     * Get payment statistics
     */
    public Map<String, Object> getPaymentStatistics() {
        try {
            System.out.println("🔍 PaymentManagementService: Starting getPaymentStatistics...");

            System.out.println("🔍 Getting totalTransactions...");
            long totalTransactions = transactionRepository.count();
            System.out.println("✅ totalTransactions: " + totalTransactions);

            System.out.println("🔍 Getting successfulTransactions...");
            long successfulTransactions = transactionRepository.countByStatus("SUCCESS");
            System.out.println("✅ successfulTransactions: " + successfulTransactions);

            System.out.println("🔍 Getting failedTransactions...");
            long failedTransactions = transactionRepository.countByStatus("FAILED");
            System.out.println("✅ failedTransactions: " + failedTransactions);

            System.out.println("🔍 Getting pendingTransactions...");
            long pendingTransactions = transactionRepository.countByStatus("PENDING");
            System.out.println("✅ pendingTransactions: " + pendingTransactions);

            System.out.println("🔍 Getting totalRevenue...");
            Double totalRevenue = transactionRepository.sumTotalFeeByStatus("SUCCESS");
            if (totalRevenue == null)
                totalRevenue = 0.0;
            System.out.println("✅ totalRevenue: " + totalRevenue);

            System.out.println("🔍 Getting recentTransactions...");
            // Get recent transactions - but don't include them in response for now to avoid
            // serialization issues
            List<TransactionInformation> recentTransactions = transactionRepository
                    .findRecentTransactions(PageRequest.of(0, 10));
            System.out.println("✅ recentTransactions count: " + recentTransactions.size());

            Map<String, Object> result = Map.of(
                    "totalTransactions", totalTransactions,
                    "successfulTransactions", successfulTransactions,
                    "failedTransactions", failedTransactions,
                    "pendingTransactions", pendingTransactions,
                    "totalRevenue", totalRevenue);

            System.out.println("✅ PaymentManagementService: Statistics completed successfully");
            return result;

        } catch (Exception e) {
            System.err.println("❌ PaymentManagementService: Error in getPaymentStatistics: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Process refund for a transaction
     */
    @Transactional
    public boolean processRefund(int transactionId, String reason, String adminUser) {
        try {
            Optional<TransactionInformation> transactionOpt = transactionRepository.findById(transactionId);
            if (!transactionOpt.isPresent()) {
                System.err.println("Transaction not found: " + transactionId);
                return false;
            }

            TransactionInformation transaction = transactionOpt.get();

            // Check if transaction is eligible for refund
            if (!"SUCCESS".equals(transaction.getStatus())) {
                System.err.println("Transaction not eligible for refund. Status: " + transaction.getStatus());
                return false;
            }

            // Check if already refunded
            if ("REFUNDED".equals(transaction.getStatus())) {
                System.err.println("Transaction already refunded: " + transactionId);
                return false;
            }

            Order order = transaction.getOrder();
            if (order == null) {
                System.err.println("No order associated with transaction: " + transactionId);
                return false;
            }

            // For now, we'll mark as refunded without calling external API
            // In a real implementation, you would call VNPay refund API here
            System.out.println("=== Processing Refund ===");
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Amount: " + transaction.getTotalFee() + " VND");
            System.out.println("Reason: " + reason);
            System.out.println("Admin User: " + adminUser);

            // Update transaction status
            transaction.setStatus("REFUNDED");
            transaction.setContent(transaction.getContent() + " | REFUNDED: " + reason + " | By: " + adminUser);
            transactionRepository.save(transaction);

            // Update order payment status
            order.setPayment(false);
            orderRepository.save(order);

            System.out.println("✅ Refund processed successfully for transaction: " + transactionId);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error processing refund for transaction " + transactionId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update transaction status
     */
    @Transactional
    public boolean updateTransactionStatus(int transactionId, String newStatus, String notes) {
        try {
            Optional<TransactionInformation> transactionOpt = transactionRepository.findById(transactionId);
            if (!transactionOpt.isPresent()) {
                return false;
            }

            TransactionInformation transaction = transactionOpt.get();
            String oldStatus = transaction.getStatus();

            transaction.setStatus(newStatus);
            if (notes != null && !notes.trim().isEmpty()) {
                transaction.setContent(transaction.getContent() + " | STATUS_UPDATE: " + oldStatus + " -> " + newStatus
                        + " | " + notes);
            }

            transactionRepository.save(transaction);

            System.out.println(
                    "✅ Transaction status updated: " + transactionId + " from " + oldStatus + " to " + newStatus);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error updating transaction status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get payments within date range
     */
    public List<TransactionInformation> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionTimeBetween(startDate, endDate);
    }

    /**
     * Get payments by payment method
     */
    public List<TransactionInformation> getPaymentsByMethod(String paymentMethod) {
        return transactionRepository.findByPaymentMethod(paymentMethod);
    }

    /**
     * Check if order has successful payment
     */
    public boolean hasSuccessfulPayment(int orderId) {
        Optional<TransactionInformation> transaction = transactionRepository.findByOrderId(orderId);
        return transaction.isPresent() && "SUCCESS".equals(transaction.get().getStatus());
    }
}
