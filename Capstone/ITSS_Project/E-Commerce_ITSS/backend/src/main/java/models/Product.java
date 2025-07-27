package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Inheritance;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

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
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private User sellerId;

    public Product() {
        // Default constructor
    }

    public Product(String title, int price, String category, String imageUrl, int quantity, LocalDate entryDate,
            double dimension, double weight, int sellerId) {
        this.title = title;
        this.price = price;
        this.category = category;
        this.imageURL = imageUrl;
        this.quantity = quantity;
        this.entry_date = entryDate;
        this.dimension = dimension;
        this.weight = weight;
    }

    // Manual getters for compatibility
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getImageURL() {
        return imageURL;
    }

    public LocalDate getEntry_date() {
        return entry_date;
    }

    public double getDimension() {
        return dimension;
    }

    public double getWeight() {
        return weight;
    }

    public User getSellerId() {
        return sellerId;
    }

    // Manual setters for compatibility
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setEntry_date(LocalDate entry_date) {
        this.entry_date = entry_date;
    }

    public void setDimension(double dimension) {
        this.dimension = dimension;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setSellerId(User sellerId) {
        this.sellerId = sellerId;
    }

}
