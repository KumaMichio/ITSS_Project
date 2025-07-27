package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(builderMethodName = "shippingMethodBuilder")
@Table(name = "shippingmethod")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private int methodID;

    @Column(name = "method_name")
    private String methodName;

    @Column(name = "is_rush")
    private boolean isRush;

    @Column(name = "shipping_fees")
    private double shippingFees;

    @OneToMany(mappedBy = "shippingMethod", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    // Explicit builder method for compatibility
    public static ShippingMethodBuilder shippingMethodBuilder() {
        return new ShippingMethodBuilder();
    }

    // Manual setters for compatibility
    public void setMethodID(int methodID) {
        this.methodID = methodID;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setRush(boolean isRush) {
        this.isRush = isRush;
    }

    public void setShippingFees(double shippingFees) {
        this.shippingFees = shippingFees;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
