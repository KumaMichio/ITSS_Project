package models;


import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Inheritance;


import java.time.LocalDate;

@Getter
@Setter
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(name = "title", nullable = false, length = 350)
    private String title;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "category", length = 20)
    private String category;

    @Column(name = "imageURL", length = 300)
    private String imageURL;

    private int quantity;

    private LocalDate entry_date;

    private double dimension;

    @Column(name = "weight")
    private double weight;

}
