package request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String username;
    private String password;

    // Manual getters for compatibility
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
