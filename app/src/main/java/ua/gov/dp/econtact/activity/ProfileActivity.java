package ua.gov.dp.econtact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.util.DateUtil;

/**
 * Created by Yalantis
 *
 * @author Oleksii Shliama.
 */
public class ProfileActivity extends BaseActivity {
    @Bind(R.id.text_view_first_name)
    TextView mTextViewFirstName;
    @Bind(R.id.text_view_middle_name)
    TextView mTextViewMiddleName;
    @Bind(R.id.text_view_last_name)
    TextView mTextViewLastName;
    @Bind(R.id.text_view_email)
    TextView mTextViewEmail;
    @Bind(R.id.text_view_phone)
    TextView mTextViewPhone;
    @Bind(R.id.text_view_birthdate)
    TextView mTextViewBirthDate;
    @Bind(R.id.text_view_address)
    TextView mTextViewAddress;
    @Bind(R.id.row_phone)
    View mRowPhone;
    @Bind(R.id.row_birthday)
    View mRowBirthday;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        showBackButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserProfileInfo();
    }

    private void setUserProfileInfo() {
        User currentUser = App.dataManager.getCurrentUser();
        if (currentUser != null) {
            mTextViewFirstName.setText(currentUser.getFirstName());
            mTextViewMiddleName.setText(currentUser.getMiddleName());
            mTextViewLastName.setText(currentUser.getLastName());
            mTextViewEmail.setText(currentUser.getEmail());
            if (TextUtils.isEmpty(currentUser.getPhone())) {
                mRowPhone.setVisibility(View.GONE);
            } else {
                mRowPhone.setVisibility(View.VISIBLE);
                mTextViewPhone.setText(currentUser.getPhone());
            }
            if (currentUser.getBirthdaySeconds() == 0) {
                mRowBirthday.setVisibility(View.GONE);
            } else {
                mRowBirthday.setVisibility(View.VISIBLE);
                mTextViewBirthDate.setText(DateUtil.getFormattedDate(currentUser.getBirthdaySeconds() * Const.MILLIS_IN_SECOND));
            }
            mTextViewAddress.setText(Address.generateAddressLabel(currentUser.getAddress()));
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
