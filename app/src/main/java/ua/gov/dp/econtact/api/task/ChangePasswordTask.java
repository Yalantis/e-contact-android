package ua.gov.dp.econtact.api.task;

import android.text.TextUtils;

import retrofit.RetrofitError;
import retrofit.client.Response;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.model.dto.ChangePasswordDTO;

/**
 * Created by cleanok on 02.06.16.
 */
public class ChangePasswordTask extends ApiTask<UserApi, Void> {

    private final ChangePasswordDTO mChangePasswordDto;
    private final Callback mCallback;

    public ChangePasswordTask(UserApi userApi, String oldPassword, String newPassword, Callback callback) {
        super(userApi);
        mCallback = callback;
        mChangePasswordDto = new ChangePasswordDTO();
        mChangePasswordDto.setOldPassword(oldPassword);
        mChangePasswordDto.setNewPassword(newPassword);

    }

    @Override
    public void onSuccess(Void aVoid, Response response) {
        if (App.dataManager.getCurrentUser() != null && !TextUtils.isEmpty(App.dataManager.getCurrentUser().getEmail())) {
            App.apiManager.login(App.dataManager.getCurrentUser().getEmail(), mChangePasswordDto.getNewPassword(), mCallback);
        } else {
            mCallback.onLoginFailed();
        }
    }

    @Override
    protected void onFailure(RetrofitError error) {
        switch (error.getResponse().getStatus()) {
            case Const.ApiCode.CODE_INCORRECT_OLD_PASSWORD: {
                mCallback.oldPasswordIncorrect();
                break;
            }
            case Const.ApiCode.CODE_LOGIN_FAIL: {
                mCallback.onLoginFailed();
                break;
            }
            default: {
                super.onFailure(error);
            }
        }
    }

    @Override
    public void run() {
        api.changePassword(mChangePasswordDto, this);
    }

    public interface Callback extends SignInTask.LoginCallback {
        void oldPasswordIncorrect();
    }
}
