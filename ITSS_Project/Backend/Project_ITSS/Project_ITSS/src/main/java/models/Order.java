package models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.EnableMBeanExport;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(builderMethodName = "orderBuilder")
@Table(name = "orders")

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    // ======= RELATION TO USER =======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ======= RELATION TO DELIVERY =======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private DeliveryInformation deliveryInformation;

    // ======= RELATION TO TRANSACTION =======
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private TransactionInformation transaction;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    // ======= RELATION TO SHIPPINGMETHOD =======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_id")
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_fees")
    private double shippingFees;

    @Column(name = "total_amount")
    private double totalAmount;

    private LocalDateTime createdAt;

    private int VAT;

    private double totalFees;

    boolean isPayment;

}
