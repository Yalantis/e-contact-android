package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aleksandr on 29.09.2015.
 */
public class FbTokenDTO {

    @SerializedName("fb_token")
    private String fbToken;

    public FbTokenDTO(final String fbToken) {
        this.fbToken = fbToken;
    }
}
