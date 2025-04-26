package models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder(builderMethodName = "shippingMethodBuilder")
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private int methodID;

    private String method_name;

    private boolean is_rush;

    private double shipping_fees;
}
