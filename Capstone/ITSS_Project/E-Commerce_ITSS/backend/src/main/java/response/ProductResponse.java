package response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
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
