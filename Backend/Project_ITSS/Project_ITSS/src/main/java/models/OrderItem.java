package models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder(builderMethodName = "orderitemBuilder")
@Table(name = "orderitem")

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    int productId;

    int quantity;

    double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // tên cột trong DB
    private Order order;

}
