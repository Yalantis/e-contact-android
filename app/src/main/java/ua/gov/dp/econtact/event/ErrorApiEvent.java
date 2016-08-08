package ua.gov.dp.econtact.event;

import android.text.TextUtils;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.model.ApiError;
import ua.gov.dp.econtact.model.dto.ErrorResponse;


/**
 * Created by Yalantis
 * 22.02.2013
 *
 * @author Ed Baev
 */
public class ErrorApiEvent extends ApiEvent<ApiError> {

    private String mMessage;
    private boolean mDisplayToast;
    private ErrorResponse mErrorResponse;

    public ErrorApiEvent(final ApiError data) {
        super(data);
        mMessage = App.getContext().getString(data.messageId);
        mDisplayToast = data.displayToast;
    }

    public ErrorApiEvent(final String message, final boolean displayToast, final ErrorResponse errorResponse) {
        super(null);
        mErrorResponse = errorResponse;
        mMessage = message;
        mDisplayToast = !TextUtils.isEmpty(message) && displayToast;
    }

    public ErrorApiEvent(final String message, final boolean displayToast) {
        super(null);
        mMessage = message;
        mDisplayToast = !TextUtils.isEmpty(message) && displayToast;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isDisplayToast() {
        return mDisplayToast;
    }

    public ErrorResponse getErrorResponse() {
        return mErrorResponse;
    }
}
