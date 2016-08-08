package ua.gov.dp.econtact.activity.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.account.AccountConstants;
import ua.gov.dp.econtact.activity.BaseActivity;

import timber.log.Timber;

//TODO: If you need you can use here any type of Activity
public class AuthenticatorActivity extends BaseActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TOKEN_TYPE = "AUTH_TOKEN_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_NEW_ACCOUNT";
    public static final String ARG_LAUNCH_LOGIN_SCREEN = "LAUNCH_LOGIN_SCREEN";
    public static final String PARAM_USER_PASS = "password";

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;
    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentificator);
        //Retrieves the AccountAuthenticatorResponse from either the intent of the icicle, if the
        //icicle is non-zero.
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        mAccountManager = AccountManager.get(getBaseContext());

        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TOKEN_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountConstants.TOKEN_TYPE_FREE_ACCESS;
        }

        if (getIntent().getBooleanExtra(ARG_LAUNCH_LOGIN_SCREEN, false)) {
            onClickButtonSignIn(null);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_CODE_AUTH && resultCode == RESULT_OK && data != null) {
            finishAuth(data);
        }
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    @Override
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }

    public void onClickButtonSignIn(final View view) {
        navigateToLoginScreen(this, getIntent().getExtras());
    }

    private void navigateToLoginScreen(final Activity activity, final Bundle extras) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtras(extras);
        activity.startActivityForResult(intent, Const.REQUEST_CODE_AUTH);
    }

    public void onClickButtonRegister(final View view) {
        navigateToRegister(this, getIntent().getExtras());
    }

    private void navigateToRegister(final Activity activity, final Bundle extras) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        intent.putExtras(extras);
        activity.startActivityForResult(intent, Const.REQUEST_CODE_AUTH);
    }

    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     *
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(final Bundle result) {
        mResultBundle = result;
    }

    public void finishAuth(final Intent intent) {
        Timber.d("finishAuth");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Timber.d("finishAuth > addAccountExplicitly");
            if (mAccountManager.addAccountExplicitly(account, accountPassword, null)) {
                //TODO: Now we support only one account
                App.accountManager.purgeAccountsExcept(account);
            } else {
                intent.getExtras().putString(AccountManager.KEY_ERROR_MESSAGE, "Account already exists");
            }
            mAccountManager.setAuthToken(account, mAuthTokenType,
                    intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
        } else {
            Timber.d("finishAuth > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
