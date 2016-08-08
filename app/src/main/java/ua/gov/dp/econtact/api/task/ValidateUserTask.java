package ua.gov.dp.econtact.api.task;

import android.accounts.AccountManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.dto.GetUserDTO;
import ua.gov.dp.econtact.model.dto.UserDto;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Yalantis
 *
 * @author <a href="mailto:iBersh20@gmail.com>Iliya Bershadskiy</a>
 * @since 26.07.15
 */
public class ValidateUserTask extends ApiTask<UserApi, GetUserDTO> {
    private UserDto mUserDto;
    private ValidateCallback mCallback;
    // TODO: remove this code if it would not be used in future versions
/*
    private AccountManager mAccountManager;
    private String mEmail;
    private String mPassword;
*/

    public ValidateUserTask(final UserApi api, final String email, final String firstName,
                            final String lastName, final String middleName, final String password,
                            final Address address, final AccountManager accountManager, final ValidateCallback callback) {
        super(api);
        mUserDto = new UserDto(email, firstName, lastName, middleName, password, address);
        // TODO: remove this code if it would not be used in future versions
        // TODO: refactor method arguments
/*
        mAccountManager = accountManager;
        mEmail = email;
        mPassword = password;
*/
        mCallback = callback;
    }

    @Override
    public void onSuccess(final GetUserDTO getUserDTO, final Response response) {
        mCallback.onValidateSuccessful();
    }

    @Override
    protected void onFailure(final RetrofitError error) {
        super.onFailure(error);
        mCallback.onValidateFailure();
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
        api.validate(element, this);
    }

    public interface ValidateCallback {
        void onValidateSuccessful();

        void onValidateFailure();
    }
}
