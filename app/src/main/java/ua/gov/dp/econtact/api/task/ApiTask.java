package ua.gov.dp.econtact.api.task;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.api.QueuedExecutorCallback;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.model.ApiError;
import ua.gov.dp.econtact.model.dto.ErrorResponse;
import ua.gov.dp.econtact.util.JsonUtils;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by: Dmitriy Dovbnya
 * Date: 21.09.13 19:34
 */
public abstract class ApiTask<T, E> implements Runnable, Callback<E> {

    protected QueuedExecutorCallback callback;
    protected T api;
    protected String apiKey;

    protected ApiTask(final T api) {
        this.api = api;
        this.apiKey = null;
    }

    protected ApiTask(final T api, final String apiKey) {
        this.api = api;
        this.apiKey = apiKey;
    }

    public void cancel() {
    }

    @Override
    public void success(final E e, final Response response) {
        onSuccess(e, response);
        finished();
    }


    @Override
    public void failure(final RetrofitError error) {
        Log.d("Failure", error.toString());
        onFailure(error);
        finished();
    }

    /**
     * @param callback for ApiTaskExecutor
     */
    public void setCallback(final QueuedExecutorCallback callback) {
        this.callback = callback;
    }

    public void finished() {
        if (callback != null) {
            callback.finished();
        }
    }

    public abstract void onSuccess(final E e, final Response response);

    protected void onFailure(final RetrofitError error) {
        ErrorResponse errorResponse = null;
        boolean isEmailError = false;
        try {
            Response response = error.getResponse();
            if (response != null) {
                TypedByteArray body = (TypedByteArray) response.getBody();
                if (body != null && body.getBytes() != null) {
                    String json = new String(body.getBytes());
                    Gson gson = new Gson();
                    errorResponse = gson.fromJson(json, ErrorResponse.class);
                    isEmailError = JsonUtils.isEmailError(json);
                }
            }
        } catch (Exception e) {
            // TODO: handle cache here
        }

        if (errorResponse == null && error.isNetworkError()) {
            EventBus.getDefault().postSticky(new ErrorApiEvent(ApiError.NO_INTERNET));
        } else if (isEmailError) {
            EventBus.getDefault().postSticky(new ErrorApiEvent(ApiError.INVALID_EMAIL));
        } else if (error.getResponse()!=null && error.getResponse().getStatus() == Const.ApiCode.CODE_TOO_MANY_REQUESTS){
            EventBus.getDefault().postSticky(new ErrorApiEvent(ApiError.TOO_MANY_REQUESTS));
        } else if (errorResponse == null) {
            EventBus.getDefault().postSticky(new ErrorApiEvent(error.getMessage(), false, new ErrorResponse()));
        } else {
            EventBus.getDefault().postSticky(new ErrorApiEvent(errorResponse.getErrorMessage(), !TextUtils.isEmpty(errorResponse.getErrorMessage()), errorResponse));
        }
    }
}
