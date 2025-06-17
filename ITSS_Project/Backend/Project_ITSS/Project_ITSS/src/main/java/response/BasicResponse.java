package response;

import lombok.Data;

@Data
public class BasicResponse {
    private String message;

    public BasicResponse(String message) {
        this.message = message;
    }
}
