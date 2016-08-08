package ua.gov.dp.econtact.api.task;

import retrofit.RetrofitError;
import retrofit.client.Response;
import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.model.dto.PushTokenDto;

/**
 * Created by cleanok on 20.05.16.
 */
public class UpdateTokenTask extends ApiTask<UserApi, Void> {

    private final String mToken;
    private final Callback mCallback;

    public UpdateTokenTask(UserApi api, String token, Callback callback) {
        super(api);
        mToken = token;
        mCallback = callback;
    }

    @Override
    public void onSuccess(Void aVoid, Response response) {
        mCallback.onSuccess();
    }

    @Override
    protected void onFailure(RetrofitError error) {
        super.onFailure(error);
        if (error != null && error.getResponse() != null) {
            mCallback.onFailure(error.getResponse().getStatus());
        }
    }

    @Override
    public void run() {
        PushTokenDto pushTokenDto = new PushTokenDto(mToken, ApiSettings.USER.DEVICE_TYPE);
        api.updateToken(pushTokenDto, this);
    }

    public interface Callback {
        void onSuccess();

        void onFailure(int statusCode);
    }
}
