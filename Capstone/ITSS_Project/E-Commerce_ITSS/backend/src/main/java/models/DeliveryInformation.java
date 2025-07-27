package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "delivery_info")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DeliveryInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String address;

    private String province;

    private String instruction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Manual getters for compatibility
    public String getName() {
        return name;
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

    public String getInstruction() {
        return instruction;
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }

    // Manual setters for compatibility
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
