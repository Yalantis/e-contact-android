package ua.gov.dp.econtact.api.task;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.google.gson.Gson;

import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.account.AccountConstants;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.dto.AuthDTO;
import ua.gov.dp.econtact.model.dto.GetUserDTO;

/**
 * Created by Yalantis
 */
public class SignInTask extends ApiTask<UserApi, GetUserDTO> {
    private String mEmail;
    private String mPassword;
    private Gson mGson;
    private AccountManager mAccountManager;
    private LoginCallback mCallback;

    public SignInTask(final UserApi api, final AccountManager accountManager, final String email,
                      final String password, final LoginCallback loginCallback) {
        super(api);
        mGson = new Gson();
        mEmail = email;
        mPassword = password;
        mAccountManager = accountManager;
        mCallback = loginCallback;
    }

    @Override
    public void onSuccess(final GetUserDTO getUserDTO, final Response response) {
        String token = getUserDTO.getToken();
        try {
            Account account = new Account(mEmail, AccountConstants.ACCOUNT_TYPE);
            mAccountManager.addAccountExplicitly(account, mPassword, null);
            mAccountManager.setAuthToken(account, AccountConstants.TOKEN_TYPE_FREE_ACCESS, token);
            App.accountManager.setAuthToken(token);
            User user = getUserDTO.getUser();
            App.dataManager.setUserId(user.getId());
            App.dataManager.saveProfileFromServerData(user);
            mCallback.onLoginSuccessful();
        } catch (SecurityException e) {
            Timber.e(e, e.getMessage());
            mCallback.onLoginFailed();
        }
    }

    @Override
    protected void onFailure(final RetrofitError error) {
        super.onFailure(error);
        mCallback.onLoginFailed();
    }

    @Override
    public void run() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setPassword(mPassword);
        authDTO.setUserName(mEmail);
        api.signIn(mGson.toJsonTree(authDTO), this);
    }

    public interface LoginCallback {

        void onLoginSuccessful();

        void onLoginFailed();
    }
}
