package ua.gov.dp.econtact.manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.account.AccountConstants;
import ua.gov.dp.econtact.util.ValidatorUtil;

/**
 * Created by Yalantis
 */
public class EContactAccountManager implements Manager {

    private AccountManager mAccountManager;
    private String mAuthToken;
    private Account mConnectedAccount;

    @Override
    public void init(final Context context) {
        mAccountManager = android.accounts.AccountManager.get(context);
        Account[] accounts = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);

        if (!ValidatorUtil.isEmptyArray(accounts)) {
            final String authToken = mAccountManager.peekAuthToken(accounts[0],
                    AccountConstants.TOKEN_TYPE_FREE_ACCESS);
            if (!TextUtils.isEmpty(authToken)) {
                App.accountManager.setAuthToken(authToken);
            }
        }
    }

    @Override
    public void clear() {
        mAuthToken = null;
        mConnectedAccount = null;
    }

    public void purgeConnectedAccount() {
        deleteAccount(mConnectedAccount);
        clear();
    }

    private void deleteAccount(final Account account) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            mAccountManager.removeAccount(account, null, null, null);
        } else {
            mAccountManager.removeAccount(account, null, null);
        }
    }

    public boolean isAccountAdded() {
        return mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE).length > 0;
    }

    public void purgeAccounts() {
        Account[] accounts = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);
        for (Account account : accounts) {
            deleteAccount(account);
        }
        clear();
    }

    public void purgeAccountsExcept(final Account exceptAccount) {
        Account[] accounts = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);
        for (Account account : accounts) {
            if (!account.equals(exceptAccount)) {
                deleteAccount(account);
            }
        }
        clear();
    }

    public void setConnectedAccount(final Account connectedAccount) {
        mConnectedAccount = connectedAccount;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(final String token) {
        this.mAuthToken = token;
    }

    /**
     * Get an auth token for the account.
     * If not exist - add it and then return its auth token.
     * If one exist - return its auth token.
     * If more than one exists - show a picker and return the select account's auth token.
     */
    public void getTokenForAccountCreateIfNeeded(final String accountType, final String authTokenType,
                                                 final Activity activity,
                                                 final AccountManagerCallback<Bundle> accountManagerCallback) {
        mAccountManager.getAuthTokenByFeatures(
                accountType, authTokenType, null, activity, null, null, accountManagerCallback, null);
    }

    public void getTokenForAccount(final Activity activity,
                                   final AccountManagerCallback<Bundle> accountManagerCallback) {
        mAccountManager.getAuthToken(
                mConnectedAccount, AccountConstants.TOKEN_TYPE_FREE_ACCESS,
                null, activity, accountManagerCallback, null);
    }

    /**
     * Invalidates the auth token for the account.
     * Must call this method when the auth token is found to have expired or otherwise become
     * invalid for authenticating requests.
     */
    public void invalidateAuthToken() {
        mAccountManager.invalidateAuthToken(AccountConstants.ACCOUNT_TYPE, mAuthToken);
        mAuthToken = null;
    }
}
