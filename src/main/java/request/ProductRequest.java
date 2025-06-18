package request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class ProductRequest {
    String title;
    int price;
    String category;
    String imageURL;
    int quantity;
    int category_id;
    LocalDate entry_date;
    double dimension;
    double weight;
}
