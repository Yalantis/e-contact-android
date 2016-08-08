package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    private static final String MESSAGE = "mMessage";

    @SerializedName(MESSAGE)
    private String errorMessage;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
