package ua.gov.dp.econtact.activity.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthConfig;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.api.task.RegisterUserTask;
import ua.gov.dp.econtact.api.task.ValidateUserTask;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.util.KeyboardUtils;
import ua.gov.dp.econtact.util.Toaster;
import ua.gov.dp.econtact.util.Translate;
import ua.gov.dp.econtact.util.UiUtil;
import ua.gov.dp.econtact.util.ValidatorUtil;


public class SignUpActivity extends BaseAuthActivity implements RegisterUserTask.RegisterSuccessfulCallback, ValidateUserTask.ValidateCallback {

    public static final int SIGN_UP_RESULT_CODE = 201;
    public static final int SIGN_UP_REQUEST_CODE = 101;
    public static final String ADDRESS_SAVE_KEY = "address_data";

    // UI
    private RelativeLayout mSignUpScreen;
    private Button mSignUpButton;
    private RelativeLayout mSignUpLayout;
    private RelativeLayout mAddressLayout;
    private EditText mFirstNameEditText, mSecondNameEditText, mMiddleNameEditText;
    private CoordinatorLayout mCoordinator;
    private TextView mAddressTextView;
    private AuthCallback authCallback;
    private Address mAddress = new Address();
    //This flag need to catch unexpected error from Digits, when any callbacks did not call
    private Bundle addressData;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        setBlurredLandscape(mSignUpScreen, R.drawable.dp_landscape);
        showBackButton();
        configureTranslucent();
        mAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = AddressActivity.newInstance(SignUpActivity.this, false);
                startActivityForResult(intent, AddressActivity.SIGN_UP_REQUEST_CODE);
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mSignUpButton.setEnabled(false);
                checkSignUpValidation();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ADDRESS_SAVE_KEY, addressData);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        addressData = savedInstanceState.getBundle(ADDRESS_SAVE_KEY);
        if (addressData != null) {
            fillAddress(addressData);
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (App.dataManager.getIsStartPhoneValidate()) {
            Toaster.showShort(SignUpActivity.this, R.string.phone_register_error);
            App.dataManager.setIsStartPhoneValidate(false);
        }
    }

    private void initViews() {
        mSignUpScreen = (RelativeLayout) findViewById(R.id.sign_up_screen);
        mCoordinator = (CoordinatorLayout) findViewById(R.id.snackbar_location);
        mAddressTextView = (TextView) findViewById(R.id.address_text_view);
        mSignUpLayout = (RelativeLayout) findViewById(R.id.sign_up_relative_layout);
        mAddressLayout = (RelativeLayout) findViewById(R.id.address_layout);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mFirstNameEditText = (EditText) findViewById(R.id.edit_text_first_name);
        mSecondNameEditText = (EditText) findViewById(R.id.edit_text_second_name);
        mMiddleNameEditText = (EditText) findViewById(R.id.edit_text_middle_name);
        View.OnFocusChangeListener namesFocusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(final View view, final boolean hasFocus) {
                if (!hasFocus) {
                    isValidName((EditText) view);
                }
            }
        };
        mFirstNameEditText.setOnFocusChangeListener(namesFocusListener);
        mMiddleNameEditText.setOnFocusChangeListener(namesFocusListener);
        mSecondNameEditText.setOnFocusChangeListener(namesFocusListener);
        mMiddleNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkSignUpValidation();
                    return true;
                }
                return false;
            }
        });
    }

    private void configureTranslucent() {
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mToolbar.getLayoutParams();
            lp.setMargins(0, UiUtil.getStatusBarHeight(this), 0, 0);
        }
    }

    private void checkSignUpValidation() {
        KeyboardUtils.hide(this);
        if (isEmailValid() && isPasswordValid() && isValidName(mFirstNameEditText)
                && isValidName(mMiddleNameEditText) && isValidName(mSecondNameEditText)) {
            if (mAddress.getDistrict() == null) {
                Toaster.share(mToolbar, R.string.error_choose_address);
            } else {
                processValidate(getEmail(), getFirstName(), getLastName(), getMiddleName(),
                        getPassword(), mAddress);

            }
        }
        mSignUpButton.setEnabled(true);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressActivity.SIGN_UP_REQUEST_CODE
                && resultCode == AddressActivity.SIGN_UP_RESULT_CODE) {
            addressData = data.getBundleExtra(AddressActivity.BUNDLE_ID);
            fillAddress(addressData);
        }

    }


    private void fillAddress(final Bundle bundle) {
        long districtId = bundle.getLong(AddressActivity.DISTRICT_ID, 0);
        long cityId = bundle.getLong(AddressActivity.CITY_ID, 0);
        long streetId = bundle.getLong(AddressActivity.STREET_ID, 0);
        long houseId = bundle.getLong(AddressActivity.HOUSE_ID, 0);

        mAddress.setCity(App.dataManager.getCityById(cityId));
        mAddress.setDistrict(App.dataManager.getDistrictById(districtId));
        mAddress.setStreet(App.dataManager.getStreetById(streetId));
        mAddress.setHouse(App.dataManager.getHouseById(houseId));
        mAddress.setFlat(bundle.getString(AddressActivity.FLAT));
        mAddressTextView.setText(Address.generateAddressLabel(mAddress));
    }


    @Override
    protected void authProcessing(final String email, final String password) {
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onEvent(final ErrorApiEvent event) {
        hideProgress();
    }

    private String getFirstName() {
        return Translate.translate(mFirstNameEditText.getText().toString().trim());
    }

    private String getLastName() {
        return Translate.translate(mSecondNameEditText.getText().toString().trim());
    }

    private String getMiddleName() {
        return Translate.translate(mMiddleNameEditText.getText().toString().trim());
    }

    private boolean isValidName(final EditText view) {
        String input = Translate.translate(view.getText().toString().trim());
        if (!ValidatorUtil.isValidName(input)) {
            mErrorHelper.setError(view, R.string.error_text_empty);
            return false;
        }
        return true;
    }

    private void processRegister(final String email, final String firstName, final String lastName,
                                 final String middleName, final String password,
                                 final Address address, final String phone) {
        App.apiManager.register(email, firstName, lastName, middleName, password, address, phone, this);
    }

    private void processValidate(final String email, final String firstName, final String lastName,
                                 final String middleName, final String password, final Address address) {
        showProgress();
        App.apiManager.validate(email, firstName, lastName, middleName, password, address, this);
    }

    @Override
    public void onRegisterSuccessful() {
        hideProgress();
        setResult(SIGN_UP_RESULT_CODE);
        finish();
    }

    @Override
    public void onRegisterFailure() {
        hideProgress();
        Toast.makeText(this, R.string.phone_taken_error, Toast.LENGTH_LONG).show();
        mSignUpButton.setEnabled(true);
        Digits.getSessionManager().clearActiveSession();
    }

    @Override
    public void onValidateSuccessful() {
        App.dataManager.setIsStartPhoneValidate(true);
        hideProgress();
        mSignUpButton.setEnabled(true);
        Digits.getSessionManager().clearActiveSession();
        authCallback = new AuthCallback() {
            public void success(final DigitsSession session, final String phoneNumber) {
                App.dataManager.setIsStartPhoneValidate(false);
                showProgress();
                processRegister(getEmail(), getFirstName(), getLastName(),
                        getMiddleName(), getPassword(), mAddress, phoneNumber);
                mSignUpButton.setEnabled(false);
            }

            @Override
            public void failure(final DigitsException exception) {
                App.dataManager.setIsStartPhoneValidate(false);
                hideProgress();
                Toaster.share(mToolbar, R.string.unexpected_try_more_error);
                Digits.getSessionManager().clearActiveSession();
                mSignUpButton.setEnabled(true);
            }
        };
        Digits.authenticate(new DigitsAuthConfig.Builder().withAuthCallBack(authCallback).withThemeResId(R.style.CustomDigitsTheme).build());
    }

    @Override
    public void onValidateFailure() {
        hideProgress();
        Toaster.share(mToolbar, R.string.email_taken_error);
        mSignUpButton.setEnabled(true);
    }
}
