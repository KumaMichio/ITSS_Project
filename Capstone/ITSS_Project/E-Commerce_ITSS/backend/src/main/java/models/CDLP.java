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
@Builder(builderMethodName = "cdlpBuilder")
@Table(name = "cdlps")
@PrimaryKeyJoinColumn(name = "product_id")
public class CDLP extends Product {

    private String artist;

    @Column(name = "record_label")
    private String recordLabel;

    @Column(name = "track_list")
    private String trackList;

    public CDLP() {
        super();
    }

    // Manual getters for compatibility
    public String getArtist() {
        return artist;
    }

    public String getRecordLabel() {
        return recordLabel;
    }

    public String getTrackList() {
        return trackList;
    }
}