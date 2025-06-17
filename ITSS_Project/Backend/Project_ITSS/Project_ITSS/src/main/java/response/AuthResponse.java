package response;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private String role;
    private String email;
    private int id;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthResponse(String accessToken, String role, String email, int id) {
        this.accessToken = accessToken;
        this.role = role;
        this.email = email;
        this.id = id;
    }
}
