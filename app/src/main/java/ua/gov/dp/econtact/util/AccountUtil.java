package ua.gov.dp.econtact.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.text.TextUtils;

import ua.gov.dp.econtact.account.AccountConstants;

/**
 * Created by Yalantis
 * 29.09.2015.
 *
 * @author Aleksandr
 */
public final class AccountUtil {

    private AccountUtil() {

    }

    public static boolean isLoggedIn(final Activity activity) {
        AccountManager accountManager = AccountManager.get(activity);
        Account[] accounts = accountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);
        return accounts != null && accounts.length > 0
                && !TextUtils.isEmpty(
                accountManager.peekAuthToken(accounts[0], AccountConstants.TOKEN_TYPE_FREE_ACCESS));
    }
}
