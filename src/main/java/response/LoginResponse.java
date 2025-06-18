package response;

import models.User;

public class LoginResponse {
    private String error;
    private User user;

    public LoginResponse(String error) {
        this.error = error;
        this.user = null;
    }

    public LoginResponse(User user) {
        this.error = null;
        this.user = user;
    }


}
