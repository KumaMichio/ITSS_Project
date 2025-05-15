package models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

import java.sql.Date;
import java.sql.Time;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Builder(builderMethodName = "dvdBuilder")
@Table(name = "dvds")
@PrimaryKeyJoinColumn(name = "product_id")
public class DVD extends Product{

    @Column(name = "discType")
    private String discType;

    @Column(name = "director")
    private String director;

    @Column(name = "runtime")
    private int runtime;

    private String studio;;

    @Column(name = "language")
    private String language;

    @Column(name = "subtitles")
    private String subtitles;

    private Date releaseDate;
}