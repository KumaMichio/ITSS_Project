package models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder(builderMethodName = "orderitemBuilder")
@Table(name = "orderitem")

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    int productId;

    int quantity;

    int price;

    @Column(nullable = true)
    int orderId;

}
