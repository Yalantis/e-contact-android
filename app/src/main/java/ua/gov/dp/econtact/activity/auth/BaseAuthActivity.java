package ua.gov.dp.econtact.activity.auth;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.BaseActivity;
import ua.gov.dp.econtact.event.AuthApiEvent;
import ua.gov.dp.econtact.model.dto.AuthDTO;
import ua.gov.dp.econtact.util.ValidatorUtil;
import ua.gov.dp.econtact.view.ErrorPopupHelper;

public abstract class BaseAuthActivity extends BaseActivity {


    protected EditText mEditTextEmail;
    protected EditText mEditTextPassword;
    protected Button mLoginButton;
    protected Button mFacebookButton;
    protected Button mSignUpButton;
    protected ImageView mForgetPassword;

    protected CallbackManager mCallbackManager;
    protected String mFacebookEmail;
    protected final ErrorPopupHelper mErrorHelper = new ErrorPopupHelper();
    protected ImageView mErrorImagePassword;
    protected ImageView mErrorImageEmail;

    @Override
    public void setContentView(final int layoutId) {
        super.setContentView(layoutId);
        initializeUI();
        initFB();
        setButtonsListeners();
    }

    /**
     * Initialize UI elements
     */
    private void initializeUI() {
        mEditTextEmail = (EditText) findViewById(R.id.edit_text_email);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mFacebookButton = (Button) findViewById(R.id.facebook_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mForgetPassword = (ImageView) findViewById(R.id.forget_password_image_view);
        mErrorImagePassword = (ImageView) findViewById(R.id.error_drawable_password);
        mErrorImageEmail = (ImageView) findViewById(R.id.error_drawable_email);
    }

    /**
     * Handle buttons click
     */
    private void setButtonsListeners() {
        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                facebookLogin();
            }
        });
        mEditTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, final boolean hasFocus) {
                if (!hasFocus) {
                    isEmailValid();
                }
            }
        });
        mEditTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, final boolean hasFocus) {
                if (!hasFocus) {
                    isPasswordValid();
                }
            }
        });
    }

    protected boolean isEmailValid() {
        if (TextUtils.isEmpty(getEmail()) || !ValidatorUtil.isValidEmail(getEmail())) {
            mErrorHelper.setError(mEditTextEmail, R.string.email_validation_error, mErrorImageEmail);
            return false;
        }
        return true;
    }

    protected boolean isPasswordValid() {
        if (TextUtils.isEmpty(getPassword()) || getPassword().length() < Const.MIN_PASS_LENGTH) {
            mErrorHelper.setError(mEditTextPassword, R.string.password_length_error, mErrorImagePassword);
            return false;
        }
        return true;
    }

    protected String getEmail() {
        return mEditTextEmail.getText().toString().trim();
    }

    protected String getPassword() {
        return mEditTextPassword.getText().toString().trim();
    }

    protected abstract void authProcessing(final String email, final String password);

    public void onEvent(final AuthApiEvent event) {
        // TODO: Remove or fix this
/*
        hideProgress();
        // saveUser(event.data.get());
        createAuthIntent(event.data);
        finish();
*/
    }

    private void createAuthIntent(final AuthDTO authDTO) {
        Intent intent = new Intent();
        // intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, authDTO.getCurrentUser().getId());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE));
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, authDTO.getToken());
        intent.putExtra(AuthenticatorActivity.PARAM_USER_PASS, authDTO.getPassword());
        setResult(RESULT_OK, intent);
    }

    protected void facebookLogin() {
        if (!isLoading()) {
            showProgress();
            LoginManager.getInstance().logInWithReadPermissions(BaseAuthActivity.this, Const.FB_PERMISSION);
        }
    }

    protected void initFB() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        App.spManager.setFbUserId(loginResult.getAccessToken().getUserId());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(BaseAuthActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                        hideProgress();
                    }

                    @Override
                    public void onError(final FacebookException exception) {
                        Toast.makeText(BaseAuthActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        hideProgress();
                    }
                });
    }

}
