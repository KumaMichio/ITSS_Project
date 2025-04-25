package models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Builder(builderMethodName = "cdlpBuilder")
@Table(name = "books")
public class CDLP extends Product{

    @Column(name = "artist")
    private String artist;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "coverType")
    private String coverType;

    private Date publicationDate;

    private int pagesNumber;

    @Column(name = "language")
    private String language;

    @Column(name = "genre")
    private String genre;
}