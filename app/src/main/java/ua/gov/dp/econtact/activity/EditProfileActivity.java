package ua.gov.dp.econtact.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.auth.AddressActivity;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.event.UserUpdatedEvent;
import ua.gov.dp.econtact.fragment.dialog.ConfirmExitDialogFragment;
import ua.gov.dp.econtact.interfaces.EditProfileActivityInterface;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.address.City;
import ua.gov.dp.econtact.model.address.District;
import ua.gov.dp.econtact.model.address.House;
import ua.gov.dp.econtact.model.address.Street;
import ua.gov.dp.econtact.util.DateUtil;
import ua.gov.dp.econtact.util.Toaster;
import ua.gov.dp.econtact.util.Translate;
import ua.gov.dp.econtact.util.ValidatorUtil;

/**
 * Created by Oleksii Shliama.
 */
public class EditProfileActivity extends BaseActivity implements EditProfileActivityInterface {

    public static final int REQUEST_CODE_EDIT_ADDRESS = 420;
    private final String ADDRESS_KEY = "address_key";
    private final static String ADDRESS_TEXT_KEY = "address_text";
    private final String DATE_KEY = "date_key";


    @Bind(R.id.edit_text_first_name)
    EditText mEditTextFirstName;
    @Bind(R.id.edit_text_middle_name)
    EditText mEditTextMiddleName;
    @Bind(R.id.edit_text_last_name)
    EditText mEditTextLastName;
    @Bind(R.id.text_view_birthdate)
    TextView mTextViewBirthDate;
    @Bind(R.id.text_view_address)
    TextView mTextViewAddress;

    // Data
    private Calendar mCalendar;
    private Realm mRealm;
    private Address mEditedAddress;
    private User mCurrentUser;

    private int mUserInfoHash;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCurrentUser = App.dataManager.getCurrentUser();
        if (savedInstanceState == null) {
            setUserProfileInfo();
            mUserInfoHash = calcUserInfoHash();
        }

        mRealm = App.dataManager.getRealm();
    }

    @OnClick(R.id.wrapper_birthdate)
    void onBirthDateClick() {
        showDatePickerDialog();
    }

    @OnClick(R.id.wrapper_address)
    void onAddressClick() {
        Intent intent = AddressActivity.newInstance(EditProfileActivity.this, false);
        startActivityForResult(intent, REQUEST_CODE_EDIT_ADDRESS);
    }

    @OnClick(R.id.wrapper_change_password)
    void onChangePasswordClick() {
        startActivity(ChangePasswordActivity.newInstance(EditProfileActivity.this));
    }


    private void showDatePickerDialog() {
        Calendar c = mCalendar == null ? Calendar.getInstance() : mCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.AppCompatAlertDialogStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {
                        if (mCalendar == null) {
                            mCalendar = Calendar.getInstance();
                        }
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        mTextViewBirthDate.setText(DateUtil.getFormattedDate(mCalendar.getTimeInMillis()));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void setUserProfileInfo() {
        if (mCurrentUser != null) {
            mEditTextFirstName.setText(mCurrentUser.getFirstName());
            mEditTextMiddleName.setText(mCurrentUser.getMiddleName());
            mEditTextLastName.setText(mCurrentUser.getLastName());
            if (mCurrentUser.getBirthdaySeconds() != 0) {
                mTextViewBirthDate.setText(DateUtil.getFormattedDate(mCurrentUser.getBirthdaySeconds() * Const.MILLIS_IN_SECOND));
                if (mCalendar == null) {
                    mCalendar = Calendar.getInstance();
                }
                mCalendar.setTimeInMillis(mCurrentUser.getBirthdaySeconds() * Const.MILLIS_IN_SECOND);
            }
            mTextViewAddress.setText(
                    Address.generateAddressLabel(
                            null != mEditedAddress ? mEditedAddress : mCurrentUser.getAddress()));
        } else {
            finish();
        }
    }

    private int calcUserInfoHash() {
        int stringCapacity = 100;
        return new StringBuilder(stringCapacity)
                .append(mEditTextFirstName.getText())
                .append(mEditTextMiddleName.getText())
                .append(mEditTextLastName.getText())
                .append(mTextViewBirthDate.getText())
                .append(mTextViewAddress.getText())
                .toString().hashCode();
    }

    private void saveUserProfileInfo() {
        String firstName = Translate.translate(mEditTextFirstName.getText().toString().trim());
        String lastName = Translate.translate(mEditTextLastName.getText().toString().trim());
        String middleName = Translate.translate(mEditTextMiddleName.getText().toString().trim());

        if (!ValidatorUtil.isValidName(firstName, lastName, middleName)) {
            Toaster.share(mToolbar, R.string.error_text_empty);
            return;
        }
        if (mEditedAddress != null && mEditedAddress.getDistrict() == null) {
            Toaster.share(mToolbar, R.string.error_choose_address);
            return;
        }

        mRealm.beginTransaction();

        mCurrentUser.setFirstName(firstName);
        mCurrentUser.setMiddleName(middleName);
        mCurrentUser.setLastName(lastName);
        if (mCalendar != null) {
            mCurrentUser.setBirthdaySeconds(mCalendar.getTimeInMillis() / Const.MILLIS_IN_SECOND);
        }
        if (null != mEditedAddress) {
            // Address was edited
            Address address = mRealm.where(Address.class).equalTo(Address.ID, mEditedAddress.getId()).findFirst();
            if (null != address) {
                address.removeFromRealm();
            }
            mCurrentUser.setAddress(mRealm.copyToRealmOrUpdate(mEditedAddress));
        }

        mRealm.commitTransaction();

        showProgress();
        App.apiManager.updateUser(mCurrentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateBack();
                return true;
            case R.id.action_save_profile:
                saveChangesAndFinish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT_ADDRESS:
                if (null != data && null != data.getExtras()) {
                    Bundle addressBundle = (Bundle) data.getExtras().get(AddressActivity.BUNDLE_ID);
                    if (null != addressBundle) {
                        mEditedAddress = new Address();

                        District district = mRealm.where(District.class).equalTo(District.ID, addressBundle.getLong(AddressActivity.DISTRICT_ID)).findFirst();
                        City city = mRealm.where(City.class).equalTo(City.ID, addressBundle.getLong(AddressActivity.CITY_ID)).findFirst();
                        Street street = mRealm.where(Street.class).equalTo(Street.ID, addressBundle.getLong(AddressActivity.STREET_ID)).findFirst();
                        House house = mRealm.where(House.class).equalTo(House.ID, addressBundle.getLong(AddressActivity.HOUSE_ID)).findFirst();
                        String flat = addressBundle.getString(AddressActivity.FLAT);

                        mEditedAddress.setDistrict(district);
                        mEditedAddress.setCity(city);
                        mEditedAddress.setStreet(street);
                        mEditedAddress.setHouse(house);
                        mEditedAddress.setFlat(flat);
                    }
                    setUserProfileInfo();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEditedAddress != null) {
            outState.putParcelable(ADDRESS_KEY, mEditedAddress);
        } else {
            outState.putString(ADDRESS_TEXT_KEY, mTextViewAddress.getText().toString());
        }
        if (mCalendar != null) {
            outState.putLong(DATE_KEY, mCalendar.getTimeInMillis());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(ADDRESS_KEY)) {
            mEditedAddress = savedInstanceState.getParcelable(ADDRESS_KEY);
            mTextViewAddress.setText(Address.generateAddressLabel(mEditedAddress));
        } else if (savedInstanceState.containsKey(ADDRESS_TEXT_KEY)) {
            mTextViewAddress.setText(savedInstanceState.getString(ADDRESS_TEXT_KEY));
        }

        if (savedInstanceState.containsKey(DATE_KEY)) {
            if (mCalendar == null) {
                mCalendar = Calendar.getInstance();
            }
            mCalendar.setTimeInMillis(savedInstanceState.getLong(DATE_KEY));
            mTextViewBirthDate.setText(DateUtil.getFormattedDate(mCalendar.getTimeInMillis()));
        }
    }

    @Override
    public void saveChangesAndFinish() {
        saveUserProfileInfo();
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    private void navigateBack() {
        if (hasProfileChanged()) {
            ConfirmExitDialogFragment.newInstance().show(getSupportFragmentManager());
        } else {
            finish();
        }
    }

    private boolean hasProfileChanged() {
        return mUserInfoHash != calcUserInfoHash();
    }

    public void onEvent(final UserUpdatedEvent event) {
        removeStickyEvent(event);
        hideProgress();
        finish();
    }

    public void onEvent(final ErrorApiEvent event) {
        removeStickyEvent(event);
        hideProgress();
        Toaster.share(mToolbar, R.string.error);
    }
}
