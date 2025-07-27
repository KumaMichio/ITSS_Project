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

    // Manual getters for compatibility
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getProvince() {
        return province;
    }
}