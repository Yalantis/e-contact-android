package ua.gov.dp.econtact.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.api.task.PasswordResetTask;
import ua.gov.dp.econtact.api.task.SignInTask;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.event.PasswordResetEvent;
import ua.gov.dp.econtact.fragment.dialog.ForgotPasswordDialogFragment;
import ua.gov.dp.econtact.interfaces.ForgotPasswordDialogListener;
import ua.gov.dp.econtact.model.dto.ErrorResponse;
import ua.gov.dp.econtact.push.RegistrationService;
import ua.gov.dp.econtact.util.KeyboardUtils;
import ua.gov.dp.econtact.util.Toaster;

public class LoginActivity extends BaseAuthActivity implements SignInTask.LoginCallback, ForgotPasswordDialogListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFB();
        mForgetPassword.setVisibility(View.VISIBLE);
        configureButtonsListeners();
        setBlurredLandscape((RelativeLayout) findViewById(R.id.login_screen), R.drawable.dp_landscape);
    }

    private void configureButtonsListeners() {
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class), SignUpActivity.SIGN_UP_REQUEST_CODE);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                KeyboardUtils.hide(LoginActivity.this);
                if (isEmailValid() && isPasswordValid()) {
                    showProgress();
                    authProcessing(getEmail(), getPassword());
                }
            }
        });

        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ForgotPasswordDialogFragment.newInstance().show(getSupportFragmentManager());
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignUpActivity.SIGN_UP_REQUEST_CODE) {
            switch (resultCode) {
                case SignUpActivity.SIGN_UP_RESULT_CODE:
                    finish();
                    break;
                default:
                    break;
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void authProcessing(final String email, final String password) {
        App.apiManager.login(email, password, this);
    }

    @Override
    public void onLoginSuccessful() {
        Intent intent = new Intent(this, RegistrationService.class);
        startService(intent);
        hideProgress();
        finish();
    }

    @Override
    public void onLoginFailed() {
        hideProgress();
        Toaster.showShort(this, R.string.login_error);
    }

    public void onEvent(PasswordResetEvent event) {
        removeStickyEvent(event);
        hideProgress();
        Toast.makeText(this, getString(R.string.password_reset_success, event.getEmail()), Toast.LENGTH_SHORT).show();
    }


    public void onEvent(final ErrorApiEvent event) {
        removeStickyEvent(event);
        hideProgress();
        ErrorResponse errResponse = event.getErrorResponse();
        if (errResponse != null) {
            switch (errResponse.getCode()) {
                case PasswordResetTask.CODE_USER_NOT_FOUND: {
                    Toaster.showShort(this, R.string.password_reset_user_not_found);
                    break;
                }
                case PasswordResetTask.CODE_INVALID_EMAIL: {
                    Toaster.showShort(this, R.string.email_validation_error);
                    break;
                }
                default: {
                    super.onEvent(event);
                }
            }
        }
    }

    @Override
    public void resetPassword(String email) {
        App.apiManager.resetPassword(email);
        showProgress();
    }
}
