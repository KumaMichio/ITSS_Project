package models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@AllArgsConstructor
@Entity
@Builder(builderMethodName = "bookBuilder")
@Table(name = "books")
@PrimaryKeyJoinColumn(name = "product_id")
public class Book extends Product {

    private String author;
    private String publisher;

    @Column(name = "cover_type")
    private String coverType;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "pages_number")
    private int pagesNumber;

    private String language;
    private String genre;

    public Book() {
        super();
    }

    // Manual getters for compatibility
    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCoverType() {
        return coverType;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public int getPagesNumber() {
        return pagesNumber;
    }

    public String getLanguage() {
        return language;
    }

    public String getGenre() {
        return genre;
    }
}
