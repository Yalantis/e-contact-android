package ua.gov.dp.econtact.model.dto;

/**
 * Created by Ed
 */
public abstract class BaseDTO {

    private Integer status;
    private String method;

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
