package ua.gov.dp.econtact.model;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;

/**
 * Created by Yalantis
 * 25.09.2014.
 *
 * @author Dmitriy Dovbnya
 */
//CHECKSTYLE:OFF
public enum ApiError {

    NO_INTERNET(0, R.string.network_error, true),
    INVALID_EMAIL(6, R.string.email_taken_error, true),
    INVALID_PASSWORD(1, R.string.password_error, true),
    TOO_MANY_REQUESTS(Const.ApiCode.CODE_TOO_MANY_REQUESTS, R.string.error_try_again, true);

    public final int code;
    public final int messageId;
    public final boolean displayToast;

    ApiError(final int code, final int messageId, final boolean displayToast) {
        this.code = code;
        this.messageId = messageId;
        this.displayToast = displayToast;
    }

    public static ApiError fromCode(final int code) {
        for (ApiError type : ApiError.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
//CHECKSTYLE:ON
