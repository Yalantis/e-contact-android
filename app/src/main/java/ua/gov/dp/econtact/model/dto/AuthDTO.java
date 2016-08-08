package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

// TODO: refactor this class
public class AuthDTO extends BaseDTO {

    private String token;
    private String password;
    @SerializedName("username")
    private String userName;
//    @Nullable
//    private User user;

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

//    public User getCurrentUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}
