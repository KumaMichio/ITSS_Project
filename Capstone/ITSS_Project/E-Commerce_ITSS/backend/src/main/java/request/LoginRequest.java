package request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    // Manual getters for compatibility
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
