package ua.gov.dp.econtact.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.activity.auth.AuthenticatorActivity;
import ua.gov.dp.econtact.util.Toaster;

import timber.log.Timber;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

public class EContactAuthenticator extends AbstractAccountAuthenticator {

    public static final int ACCOUNT_ADDED_ERROR_CODE = 509;
    private final Context mContext;

    public EContactAuthenticator(final Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(final AccountAuthenticatorResponse response, final String accountType,
                             final String authTokenType, final String[] requiredFeatures,
                             final Bundle options) throws NetworkErrorException {
        Timber.d("addAccount");
        if (App.accountManager.isAccountAdded()) {
            final Bundle bundle = new Bundle();
            bundle.putInt(AccountManager.KEY_ERROR_CODE, ACCOUNT_ADDED_ERROR_CODE);
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, "Account is added already");
            Handler handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toaster.showShort("Account is added already");
                }
            });
            return bundle;
        }

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TOKEN_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_LAUNCH_LOGIN_SCREEN, false);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(final AccountAuthenticatorResponse response, final Account account,
                                     final Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account,
                               final String authTokenType, final Bundle options) throws NetworkErrorException {
        Timber.d("getAuthToken");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountConstants.TOKEN_TYPE_FREE_ACCESS)
                && !authTokenType.equals(AccountConstants.TOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        Timber.d("peekAuthToken returned - %s ",authToken);

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = getAuthenticationActivityIntent(response, account, authTokenType, mContext);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @NonNull
    private static Intent getAuthenticationActivityIntent(AccountAuthenticatorResponse response, Account account, String authTokenType, Context context) {
        final Intent intent = new Intent(context, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TOKEN_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
        intent.putExtra(AuthenticatorActivity.ARG_LAUNCH_LOGIN_SCREEN, true);
        return intent;
    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        switch (authTokenType) {
            case AccountConstants.TOKEN_TYPE_FREE_ACCESS:
                return AccountConstants.TOKEN_TYPE_FREE_ACCESS_LABEL;
            case AccountConstants.TOKEN_TYPE_FULL_ACCESS:
                return AccountConstants.TOKEN_TYPE_FULL_ACCESS_LABEL;
            default:
                return null;
        }
    }

    @Override
    public Bundle updateCredentials(final AccountAuthenticatorResponse response, final Account account,
                                    final String authTokenType, final Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account,
                              final String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
