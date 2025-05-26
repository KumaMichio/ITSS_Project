package models;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "delivery_info")
public class DeliveryInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    String phone;

    String address;

    String province;

    String instruction;

    int userId;
}
