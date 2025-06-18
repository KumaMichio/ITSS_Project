package dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestDeliveryInfoDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String province;
}