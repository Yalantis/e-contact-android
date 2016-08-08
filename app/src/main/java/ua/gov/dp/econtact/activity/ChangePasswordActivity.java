package ua.gov.dp.econtact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.Digits;

import butterknife.Bind;
import butterknife.ButterKnife;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.api.task.ChangePasswordTask;
import ua.gov.dp.econtact.api.task.SignInTask;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.view.ErrorPopupHelper;

/**
 * Created by cleanok on 02.06.16.
 */
public class ChangePasswordActivity extends BaseActivity implements ChangePasswordTask.Callback, SignInTask.LoginCallback {
    private final ErrorPopupHelper mErrorHelper = new ErrorPopupHelper();
    @Bind(R.id.edit_text_new_password)
    EditText mEditTextNewPassword;
    @Bind(R.id.edit_text_old_password)
    EditText mEditTextOldPassword;
    private MenuItem mMenuChangePassword;

    public static Intent newInstance(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    public void oldPasswordIncorrect() {
        hideProgress();
        Toast.makeText(ChangePasswordActivity.this, R.string.old_password_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSuccessful() {
        hideProgress();
        Toast.makeText(ChangePasswordActivity.this, R.string.change_password_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(ChangePasswordActivity.this, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
        App.apiManager.logout();
        //Just in case logout is unsuccessful - remove all user data from local storage
        App.accountManager.purgeAccounts();
        App.dataManager.deleteTicketByState(TicketStates.DRAFT.getStates());
        App.dataManager.deleteTicketByState(TicketStates.MY_TICKET.getStates());
        App.dataManager.deleteUser();
        App.spManager.clearId();
        App.spManager.clearFbId();
        Digits.getSessionManager().clearActiveSession();

        Intent intent = MainActivity.newInstance(this, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        showBackButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        mMenuChangePassword = menu.findItem(R.id.action_change_password);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.action_change_password: {
                if (isPasswordValid(mEditTextOldPassword) && isPasswordValid(mEditTextNewPassword)) {
                    App.apiManager.changePassword(getPassword(mEditTextOldPassword), getPassword(mEditTextNewPassword), this);
                    showProgress();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showProgress() {
        super.showProgress();
        mMenuChangePassword.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
        mMenuChangePassword.setEnabled(true);
    }

    private boolean isPasswordValid(EditText passwordEditText) {
        if (TextUtils.isEmpty(getPassword(passwordEditText)) || getPassword(passwordEditText).length() < Const.MIN_PASS_LENGTH) {
            mErrorHelper.setError(passwordEditText, R.string.password_length_error);
            return false;
        }
        return true;
    }

    private String getPassword(EditText passwordEditText) {
        return passwordEditText.getText().toString().trim();
    }

}
