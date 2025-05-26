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
@NoArgsConstructor
@Entity
@Builder(builderMethodName = "bookBuilder")
@Table(name = "books")
@PrimaryKeyJoinColumn(name = "product_id")
public class Book extends Product{

    private String author;
    private String publisher;
    private String coverType;
    private Date publicationDate;
    private int pagesNumber;
    private String language;
    private String genre;
}
