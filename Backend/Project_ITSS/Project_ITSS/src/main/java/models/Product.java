package models;


import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Inheritance;


import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sellerId;

    public Product(String title, int price, String category, String imageUrl, int quantity, LocalDate entryDate, double dimension, double weight, int sellerId) {
        this.title = title;
        this.price = price;
        this.category = category;
        this.imageURL = imageUrl;
        this.quantity = quantity;
        this.entry_date = entryDate;
        this.dimension = dimension;
        this.weight = weight;
    }

}
