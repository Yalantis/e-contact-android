package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cleanok on 26.05.16.
 */
public class PasswordResetDTO {
    @SerializedName("email")
    private String mEmail;

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
}
