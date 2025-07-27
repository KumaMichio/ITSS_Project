package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transactioninformation")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TransactionInformation {
    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = true)
    private Order order;

    @Column(name = "total_fee")
    private double totalFee;

    @Column(name = "status")
    private String status;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "content")
    private String content;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "vnp_transaction_no")
    private String vnpTransactionNo;

    @Column(name = "vnp_bank_code")
    private String vnpBankCode;

    @Column(name = "vnp_bank_tran_no")
    private String vnpBankTranNo;

    @Column(name = "vnp_response_code")
    private String vnpResponseCode;

    // Store the original VNPay transaction reference for cases where order_id is
    // too large
    @Column(name = "order_reference")
    private String orderReference;

    // Manual builder for compatibility
    public static TransactionInformationBuilder builder() {
        return new TransactionInformationBuilder();
    }

    // Manual getters for compatibility
    public int getTransactionId() {
        return transactionId;
    }

    public Order getOrder() {
        return order;
    }

    public String getVnpTransactionNo() {
        return vnpTransactionNo;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    // Manual setters for compatibility
    public void setStatus(String status) {
        this.status = status;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setVnpTransactionNo(String vnpTransactionNo) {
        this.vnpTransactionNo = vnpTransactionNo;
    }

    public void setVnpBankCode(String vnpBankCode) {
        this.vnpBankCode = vnpBankCode;
    }

    public void setVnpBankTranNo(String vnpBankTranNo) {
        this.vnpBankTranNo = vnpBankTranNo;
    }

    public void setVnpResponseCode(String vnpResponseCode) {
        this.vnpResponseCode = vnpResponseCode;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

}
