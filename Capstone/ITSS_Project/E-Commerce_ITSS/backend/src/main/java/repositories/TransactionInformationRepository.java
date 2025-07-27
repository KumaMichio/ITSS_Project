package repositories;

import models.Order;
import models.TransactionInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionInformationRepository extends JpaRepository<TransactionInformation, Integer> {

    Optional<TransactionInformation> findByOrderReference(String orderReference);

    List<TransactionInformation> findByStatus(String status);

    Optional<TransactionInformation> findByVnpTransactionNo(String vnpTransactionNo);

    // Find transaction by order_id - useful for checking if order has transaction
    List<TransactionInformation> findByOrder_OrderId(int orderId);

    // Additional methods for payment management

    // Find transaction by order
    Optional<TransactionInformation> findByOrder(Order order);

    // Find transaction by order ID
    @Query("SELECT t FROM TransactionInformation t WHERE t.order.orderId = :orderId")
    Optional<TransactionInformation> findByOrderId(@Param("orderId") int orderId);

    // Find transactions by payment method
    List<TransactionInformation> findByPaymentMethod(String paymentMethod);

    // Find transactions within date range
    @Query("SELECT t FROM TransactionInformation t WHERE t.transactionTime BETWEEN :startDate AND :endDate")
    List<TransactionInformation> findByTransactionTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get all transactions with pagination and sorting
    Page<TransactionInformation> findAll(Pageable pageable);

    // Find transactions by status with pagination
    Page<TransactionInformation> findByStatus(String status, Pageable pageable);

    // Search transactions by order info or customer info
    @Query("SELECT t FROM TransactionInformation t " +
            "WHERE t.content LIKE CONCAT('%', :searchTerm, '%') " +
            "OR t.order.customerFullName LIKE CONCAT('%', :searchTerm, '%') " +
            "OR t.order.customerEmail LIKE CONCAT('%', :searchTerm, '%')")
    Page<TransactionInformation> searchTransactions(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count transactions by status
    @Query("SELECT COUNT(t) FROM TransactionInformation t WHERE t.status = :status")
    long countByStatus(@Param("status") String status);

    // Sum total fee by status
    @Query("SELECT SUM(t.totalFee) FROM TransactionInformation t WHERE t.status = :status")
    Double sumTotalFeeByStatus(@Param("status") String status);

    // Get recent transactions
    @Query("SELECT t FROM TransactionInformation t ORDER BY t.transactionTime DESC")
    List<TransactionInformation> findRecentTransactions(Pageable pageable);
}
