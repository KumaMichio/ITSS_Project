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
@Data
@Builder(builderMethodName = "cdlpBuilder")
@Table(name = "cdlps")
@PrimaryKeyJoinColumn(name = "product_id")
public class CDLP extends Product{

    private String artist;
    private String recordLabel;
    private String trackList;
}