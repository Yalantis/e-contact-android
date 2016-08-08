package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cleanok on 02.06.16.
 */
public class ChangePasswordDTO {
    @SerializedName("old_password")
    private String mOldPassword;
    @SerializedName("new_password")
    private String mNewPassword;

    public String getOldPassword() {
        return mOldPassword;
    }

    public void setOldPassword(String oldPassword) {
        mOldPassword = oldPassword;
    }

    public String getNewPassword() {
        return mNewPassword;
    }

    public void setNewPassword(String newPassword) {
        mNewPassword = newPassword;
    }
}
