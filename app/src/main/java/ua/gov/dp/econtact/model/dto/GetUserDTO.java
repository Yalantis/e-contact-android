package ua.gov.dp.econtact.model.dto;

import ua.gov.dp.econtact.model.User;

public class GetUserDTO extends BaseDTO {

    private User user;
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }
}
