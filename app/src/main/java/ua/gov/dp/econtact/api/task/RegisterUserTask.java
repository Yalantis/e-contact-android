package ua.gov.dp.econtact.api.task;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.account.AccountConstants;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.dto.GetUserDTO;
import ua.gov.dp.econtact.model.dto.UserDto;

import io.realm.Realm;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yalantis
 *
 * @author <a href="mailto:iBersh20@gmail.com>Iliya Bershadskiy</a>
 * @since 26.07.15
 */
public class RegisterUserTask extends ApiTask<UserApi, GetUserDTO> {
    private UserDto mUserDto;
    private RegisterSuccessfulCallback mCallback;
    private AccountManager mAccountManager;
    private String mEmail;
    private String mPassword;

    public RegisterUserTask(final UserApi api, final String email, final String firstName,
                            final String lastName, final String middleName, final String password,
                            final Address address, final String phone,
                            final AccountManager accountManager, final RegisterSuccessfulCallback callback) {
        super(api);
        mUserDto = new UserDto(email, firstName, lastName, middleName, password, address, phone);
        mAccountManager = accountManager;
        mEmail = email;
        mPassword = password;
        mCallback = callback;
    }

    @Override
    public void onSuccess(final GetUserDTO getUserDTO, final Response response) {
        User user = getUserDTO.getUser();
        App.dataManager.setUserId(user.getId());
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
        realm.close();

        String token = getUserDTO.getToken();
        Account account = new Account(mEmail, AccountConstants.ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(account, mPassword, null);
        mAccountManager.setAuthToken(account, AccountConstants.TOKEN_TYPE_FREE_ACCESS, token);
        App.accountManager.setAuthToken(token);
        mCallback.onRegisterSuccessful();
    }

    @Override
    protected void onFailure(final RetrofitError error) {
        super.onFailure(error);
        mCallback.onRegisterFailure();
    }

    @Override
    public void run() {
        Gson gson;
        try {
            gson = App.apiManager.getGsonBuilder().create();
        } catch (ClassNotFoundException e) {
            gson = new Gson();
        }
        JsonElement element = gson.toJsonTree(mUserDto);
        api.register(element, this);
    }

    public interface RegisterSuccessfulCallback {
        void onRegisterSuccessful();

        void onRegisterFailure();
    }
}
