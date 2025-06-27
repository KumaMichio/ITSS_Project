package models;

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

}
