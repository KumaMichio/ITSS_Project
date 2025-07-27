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
@Builder(builderMethodName = "dvdBuilder")
@Table(name = "dvds")
@PrimaryKeyJoinColumn(name = "product_id")
public class DVD extends Product {

    @Column(name = "disc_type")
    private String discType;

    private String director;

    private int runtime;

    private String studio;

    private String language;

    private String subtitles;

    @Column(name = "release_date")
    private Date releaseDate;

    public DVD() {
        super();
    }

    // Manual getters for compatibility
    public String getDiscType() {
        return discType;
    }

    public String getDirector() {
        return director;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getStudio() {
        return studio;
    }

    public String getLanguage() {
        return language;
    }

    public String getSubtitles() {
        return subtitles;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}