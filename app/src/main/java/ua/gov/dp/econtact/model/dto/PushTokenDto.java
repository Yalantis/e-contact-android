package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cleanok on 23.05.16.
 */
public class PushTokenDto {
    @SerializedName("push_token")
    private String pushToken;
    @SerializedName("device_type")
    private int deviceType;

    public PushTokenDto(String pushToken, int deviceType) {
        this.pushToken = pushToken;
        this.deviceType = deviceType;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
