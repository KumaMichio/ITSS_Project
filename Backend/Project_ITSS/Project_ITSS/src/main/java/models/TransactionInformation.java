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
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
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

}
