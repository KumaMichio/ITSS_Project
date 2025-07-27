package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(builderMethodName = "orderBuilder")
@Table(name = "orders")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    // ======= RELATION TO USER (nullable for guest orders) =======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // ======= TRANSACTION ID as String =======
    @Column(name = "transaction_id", length = 20)
    private String transactionId;

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    // ======= RELATION TO SHIPPINGMETHOD =======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_id", nullable = false)
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_fees", nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingFees;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_payment", nullable = false)
    private boolean isPayment;

    // ======= NEW CUSTOMER INFO FIELDS =======
    @Column(name = "customer_email", length = 100, nullable = false)
    private String customerEmail;

    @Column(name = "customer_full_name", length = 100, nullable = false)
    private String customerFullName;

    @Column(name = "customer_phone", length = 20, nullable = false)
    private String customerPhone;

    @Column(name = "delivery_address", columnDefinition = "TEXT", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_province", length = 50, nullable = false)
    private String deliveryProvince;

    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    // Deprecated field - keeping for backward compatibility
    @Deprecated
    public int getVAT() {
        return 0; // VAT is now calculated as part of total fees
    }

    @Deprecated
    public void setVAT(int VAT) {
        // Do nothing - VAT is now calculated
    }

    // Explicit methods for compatibility
    public void setPayment(boolean payment) {
        this.isPayment = payment;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Explicit builder method for compatibility
    public static OrderBuilder orderBuilder() {
        return new OrderBuilder();
    }

    // Manual getters for compatibility
    public int getOrderId() {
        return orderId;
    }

    public double getTotalAmount() {
        return totalAmount != null ? totalAmount.doubleValue() : 0.0;
    }

    public double getShippingFees() {
        return shippingFees != null ? shippingFees.doubleValue() : 0.0;
    }

    public User getUser() {
        return user;
    }

    public boolean isPayment() {
        return isPayment;
    }

    // Manual setters for compatibility
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public void setShippingFees(double shippingFees) {
        this.shippingFees = BigDecimal.valueOf(shippingFees);
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = BigDecimal.valueOf(totalAmount);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setDeliveryProvince(String deliveryProvince) {
        this.deliveryProvince = deliveryProvince;
    }

    public void setSubtotalAmount(double subtotalAmount) {
        this.subtotalAmount = BigDecimal.valueOf(subtotalAmount);
    }

    // BigDecimal compatible setters
    public void setShippingFees(BigDecimal shippingFees) {
        this.shippingFees = shippingFees;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setSubtotalAmount(BigDecimal subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
