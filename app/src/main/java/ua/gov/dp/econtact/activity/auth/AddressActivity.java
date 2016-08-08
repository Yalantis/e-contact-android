package ua.gov.dp.econtact.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.BaseActivity;
import ua.gov.dp.econtact.adapter.AddressListAdapter;
import ua.gov.dp.econtact.event.address.CityEvent;
import ua.gov.dp.econtact.event.address.DistrictEvent;
import ua.gov.dp.econtact.event.address.HouseEvent;
import ua.gov.dp.econtact.event.address.StreetEvent;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.address.BaseAddress;
import ua.gov.dp.econtact.model.address.City;
import ua.gov.dp.econtact.model.address.CityDistrict;
import ua.gov.dp.econtact.model.address.District;
import ua.gov.dp.econtact.model.address.House;
import ua.gov.dp.econtact.model.address.Street;
import ua.gov.dp.econtact.model.address.StreetType;
import ua.gov.dp.econtact.util.AlphanumComparator;
import ua.gov.dp.econtact.util.Connectivity;
import ua.gov.dp.econtact.util.Toaster;
import ua.gov.dp.econtact.util.ValidatorUtil;
import ua.gov.dp.econtact.view.ErrorPopupHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Yalantis
 */
public class AddressActivity extends BaseActivity {

    public static final int SIGN_UP_REQUEST_CODE = 101;
    public static final int SIGN_UP_RESULT_CODE = 201;
    public static final int STREETS_SIZE_LIMIT = 10;
    public static final String DISTRICT_ID = "district_id";
    public static final String CITY_ID = "city_id";
    public static final String STREET_ID = "street_id";
    public static final String HOUSE_ID = "house_id";
    public static final String FLAT = "flat";
    public static final String BUNDLE_ID = "bundle_id";

    private final static String KEY_NEW_TICKET = "new_ticket";

    // UI
    private AutoCompleteTextView mDistrictTextView;
    private AutoCompleteTextView mStreetTextView;
    private AutoCompleteTextView mHouseTextView;
    private EditText mFlatTextView;
    private AutoCompleteTextView mCityTextView;

    private RelativeLayout mCityLayout, mStreetLayout;
    private LinearLayout mHouseLayout;
    private ImageView mClearCityImageView, mClearDistrictImageView, mClearStreetImageView;
    private ProgressBar mCityProgressBar, mDistrictProgressBar, mStreetProgressBar;
    // Data
    private List<BaseAddress> mDistricts;
    private List<BaseAddress> mCities;
    private List<BaseAddress> mStreets;
    private List<BaseAddress> mHouses;
    private long mDistrictId, mCityId, mStreetId, mHouseId;
    private final ErrorPopupHelper mErrorHelper = new ErrorPopupHelper();
    // Menu
    private MenuItem mMenuDone;

    public static Intent newInstance(final Context context, final boolean isFromNewTicket) {
        Intent intent = new Intent(context, AddressActivity.class);
        intent.putExtra(KEY_NEW_TICKET, isFromNewTicket);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        setUI();
        checkData();
        initData();
        listeners();
        setUserProfileInfo();
        setDistrictAdapter();
        User currentUser = App.dataManager.getCurrentUser();
        if (App.spManager.getUserId() != 0 && currentUser != null) {
            fillFieldsFromAddress(currentUser.getAddress());
        }
    }

    private void fillFieldsFromAddress(final Address address) {
        //Need to change feature for editing profile feature
        boolean fillOnlyDistrictAndCity = true; /*getIntent().getBooleanExtra(KEY_NEW_TICKET, false);*/
        // District
        District district = address.getDistrict();
        mDistrictId = district.getId();
        setupField(mDistrictTextView, district.getTitle());

        if (!fillOnlyDistrictAndCity) {
            // City
            mClearDistrictImageView.setVisibility(View.VISIBLE);
            mCityLayout.setVisibility(View.VISIBLE);
            mCityLayout.requestLayout();
            RealmResults<City> rawCities = Realm.getDefaultInstance().where(City.class).equalTo(City.DISTRICT_ID, mDistrictId).findAll();
            mCities = convertCityToBase(rawCities);
            setCityAdapter();
            City city = address.getCity();
            mCityId = city.getId();
            setupField(mCityTextView, city.getTitle());
            mClearCityImageView.setVisibility(View.VISIBLE);
            // Streets
            Street street = address.getStreet();
            mStreetId = street.getId();
            setupField(mStreetTextView, street.getName());
            RealmResults<Street> rawStreets = Realm.getDefaultInstance().where(Street.class).equalTo(Street.CITY_ID, mCityId).findAll();
            mStreets = convertStreetToBase(rawStreets);
            setStreetAdapter();
            mStreetLayout.setVisibility(View.VISIBLE);
            mStreetLayout.requestLayout();
            mClearStreetImageView.setVisibility(View.VISIBLE);
            // Houses
            RealmResults<House> rawHouses = Realm.getDefaultInstance().where(House.class).equalTo(House.STREET_ID, mStreetId).findAll();
            mHouses = convertHouseToBase(rawHouses);
            setHouseAdapter();
            House house = address.getHouse();
            mHouseId = house.getId();
            mHouseTextView.setText(house.getName());
            mHouseLayout.setVisibility(View.VISIBLE);
            // Flat
            String flat = address.getFlat();
            mFlatTextView.setText(flat);
            mFlatTextView.setVisibility(View.VISIBLE);
        } else {
            getCitiesByDistrictId();
        }
    }

    private void setupField(final AutoCompleteTextView target, final CharSequence text) {
        target.setText(text);
        target.setListSelection(0);
        target.dismissDropDown();
        target.setEnabled(false);
    }

    private void listeners() {
        mClearCityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideField(mStreetLayout, mCityTextView);
                hideField(mHouseLayout, mStreetTextView);
                mFlatTextView.setVisibility(View.GONE);
                mCityTextView.requestFocus();
                clearCity();
            }
        });
        mClearDistrictImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideField(mStreetLayout, mDistrictTextView);
                hideField(mCityLayout, mCityTextView);
                hideField(mHouseLayout, mStreetTextView);
                mFlatTextView.setVisibility(View.GONE);
                mDistrictTextView.requestFocus();
                clearDistrict();
            }
        });
        mClearStreetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideField(mHouseLayout, mStreetTextView);
                mFlatTextView.setVisibility(View.GONE);
                if (mStreets.size() > STREETS_SIZE_LIMIT) {
                    mStreetTextView.requestFocus();
                }
                clearStreet();
            }
        });
    }

    private void hideField(final View view, final AutoCompleteTextView autoCompleteTextView) {
        view.setVisibility(View.GONE);
        autoCompleteTextView.setText("");
        autoCompleteTextView.setFocusableInTouchMode(true);
        autoCompleteTextView.setEnabled(true);
    }

    private void clearHouseAndFlat() {
        mHouseTextView.setText("");
        mHouseId = 0;
        mFlatTextView.setText("");
        if (mMenuDone != null) {
            mMenuDone.setVisible(false);
        }
    }

    private void clearDistrict() {
        clearCity();
        mDistrictId = 0;
        mClearDistrictImageView.setVisibility(View.INVISIBLE);
    }

    private void clearCity() {
        clearStreet();
        mCityId = 0;
        mClearCityImageView.setVisibility(View.INVISIBLE);
    }

    private void clearStreet() {
        mStreetId = 0;
        clearHouseAndFlat();
        mClearStreetImageView.setVisibility(View.INVISIBLE);
    }

    private void checkData() {
        if (App.dataManager.getAllDistricts().isEmpty()) {
            Toaster.share(mToolbar, R.string.error_not_loaded);
        }
    }

    private void initData() {
        mDistricts = convertDistrictToBase(App.dataManager.getAllDistricts());
    }

    private void setDistrictAdapter() {
        mDistrictTextView.setAdapter(null);
        mDistrictTextView.setFocusableInTouchMode(false);
        mDistrictTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                clearCity();
                if (mDistricts.isEmpty()) {
                    if (Connectivity.isDeviceOnline(AddressActivity.this)) {
                        App.apiManager.getDistricts();
                        mDistrictProgressBar.setVisibility(View.VISIBLE);
                        mDistrictTextView.setEnabled(false);
                    } else {
                        Toaster.share(mToolbar, R.string.error_not_loaded);
                    }
                } else {
                    callAddressDialog(mDistricts, new IAddressChosen() {
                        @Override
                        public void onItemClick(final BaseAddress baseAddress) {
                            mClearDistrictImageView.performClick();
                            District districtOrCity = getDistrict(baseAddress.getId());
                            if (districtOrCity != null) {
                                if (districtOrCity.getDistrict() == null) {
                                    // district
                                    mDistrictTextView.setEnabled(false);
                                    mDistrictTextView.setFocusableInTouchMode(false);
                                    mDistrictTextView.setText(baseAddress.getTitle());
                                    mDistrictId = baseAddress.getId();
                                    getCitiesByDistrictId();
                                } else {
                                    // city
                                    mDistrictTextView.setText(districtOrCity.getDistrict().getTitle());
                                    mDistrictId = districtOrCity.getDistrict().getId();
                                    mCityLayout.setVisibility(View.VISIBLE);
                                    mClearDistrictImageView.setVisibility(View.VISIBLE);
                                    mCityTextView.setText(districtOrCity.getTitle());
                                    saveCity(districtOrCity);
                                    onCityClick(baseAddress);
                                }
                            }
                        }
                    }, getString(R.string.city_field_hint), mDistrictId);
                }
            }
        });
    }

    private void saveCity(final District district) {
        City city = new City();
        city.setId(district.getId());
        city.setTitle(district.getTitle());
        city.setDistrictId(district.getDistrict().getId());
        App.dataManager.saveCitiesFromServerData(Collections.singletonList(city));
    }

    private District getDistrict(final long id) {
        for (District district : App.dataManager.getAllDistricts()) {
            if (district.getId() == id) {
                return district;
            }
        }
        return null;
    }

    private void getCitiesByDistrictId() {
        mDistrictTextView.setEnabled(false);
        mDistrictProgressBar.setVisibility(View.VISIBLE);
        App.apiManager.getCities(mDistrictId);
    }

    public void onEvent(final DistrictEvent event) {
        removeStickyEvent(event);
        mDistrictProgressBar.setVisibility(View.GONE);
        mDistrictTextView.setEnabled(true);
        initData();
        mDistrictTextView.performClick();
    }

    public void onEvent(final CityEvent event) {
        removeStickyEvent(event);
        mCityLayout.setVisibility(View.VISIBLE);
        mClearDistrictImageView.setVisibility(View.VISIBLE);
        mDistrictProgressBar.setVisibility(View.GONE);
        mDistrictTextView.setFocusableInTouchMode(false);
        mDistrictTextView.setEnabled(true);
        mCityTextView.requestFocus();
        mCities = convertCityToBase(App.dataManager.getCitiesByDistrictId(mDistrictId));
        setCityAdapter();
    }

    private void setStreetAdapter() {
        if (mStreets.size() > STREETS_SIZE_LIMIT) {
            mStreetTextView.setAdapter(new AddressListAdapter(this, mStreets));
            mStreetTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view,
                                        final int position, final long id) {
                    mStreetTextView.setFocusableInTouchMode(false);
                    mStreetTextView.setEnabled(false);
                    mStreetTextView.setSelection(0);
                    mStreetProgressBar.setVisibility(View.VISIBLE);
                    mStreetId = ((BaseAddress) parent.getItemAtPosition(position)).getId();
                    App.apiManager.getHouses(mStreetId);
                }
            });
            mStreetTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // TODO: handle click or remove this
                }
            });
        } else {
            mStreetTextView.setAdapter(null);
            mStreetTextView.setFocusableInTouchMode(false);
            mStreetTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    clearHouseAndFlat();
                    callAddressDialog(mStreets, new IAddressChosen() {
                        @Override
                        public void onItemClick(final BaseAddress baseAddress) {
                            mHouseLayout.setVisibility(View.VISIBLE);
                            mStreetProgressBar.setVisibility(View.VISIBLE);
                            mStreetTextView.setEnabled(false);
                            mStreetTextView.setText(baseAddress.getTitle());
                            mStreetTextView.setFocusableInTouchMode(false);
                            mStreetTextView.setSelection(0);
                            mStreetId = baseAddress.getId();
                            App.apiManager.getHouses(mStreetId);
                        }
                    }, getString(R.string.street_field_hint), mStreetId);
                }
            });
        }
    }

    private void setCityAdapter() {
        mCityTextView.setAdapter(new AddressListAdapter(this, mCities));
        mCityTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                onCityClick((BaseAddress) parent.getItemAtPosition(position));
            }
        });
    }

    private void onCityClick(final BaseAddress street) {
        mCityTextView.setEnabled(false);
        mCityProgressBar.setVisibility(View.VISIBLE);
        mCityId = street.getId();
        App.apiManager.getStreets(mCityId);
    }

    public void onEvent(final StreetEvent event) {
        removeStickyEvent(event);
        if (mCityLayout.getVisibility() == View.VISIBLE) {
            mStreetLayout.setVisibility(View.VISIBLE);
            mCityProgressBar.setVisibility(View.GONE);
            mClearCityImageView.setVisibility(View.VISIBLE);
            mStreetTextView.requestFocus();
            mStreets = convertStreetToBase(App.dataManager.getStreetsByCityId(mCityId));
            setStreetAdapter();
        }
    }

    public void onEvent(final HouseEvent event) {
        removeStickyEvent(event);
        if (mStreetLayout.getVisibility() == View.VISIBLE) {
            mStreetTextView.setEnabled(true);
            mStreetProgressBar.setVisibility(View.GONE);
            mHouseLayout.setVisibility(View.VISIBLE);
            mClearStreetImageView.setVisibility(View.VISIBLE);
            mHouses = convertHouseToBase(App.dataManager.getHousesByStreetId(mStreetId));
            setHouseAdapter();
        }
    }

    private void setHouseAdapter() {
        mHouseTextView.setFocusableInTouchMode(false);
        mHouseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                callAddressDialog(mHouses, new IAddressChosen() {
                    @Override
                    public void onItemClick(final BaseAddress baseAddress) {
                        mMenuDone.setVisible(true);
                        mHouseId = baseAddress.getId();
                        mFlatTextView.setVisibility(View.VISIBLE);
                        mHouseTextView.setText(baseAddress.getTitle());
                    }
                }, getString(R.string.house_field_hint), mHouseId);
            }
        });

    }

    private void setUI() {
        mToolbar.setNavigationIcon(R.drawable.ic_close);
        mDistrictTextView = (AutoCompleteTextView) findViewById(R.id.city_text_view);
        mStreetTextView = (AutoCompleteTextView) findViewById(R.id.street_text_view);
        mHouseTextView = (AutoCompleteTextView) findViewById(R.id.house_text_view);
        mFlatTextView = (EditText) findViewById(R.id.flat_text_view);
        mCityTextView = (AutoCompleteTextView) findViewById(R.id.locality_ac_text_view);
        mCityLayout = (RelativeLayout) findViewById(R.id.relative_city);
        mStreetLayout = (RelativeLayout) findViewById(R.id.relative_street);
        mClearCityImageView = (ImageView) findViewById(R.id.image_view_clear_city);
        mClearDistrictImageView = (ImageView) findViewById(R.id.image_view_clear_district);
        mClearStreetImageView = (ImageView) findViewById(R.id.image_view_clear_street);
        mHouseLayout = (LinearLayout) findViewById(R.id.linear_house);
        mCityProgressBar = (ProgressBar) findViewById(R.id.progress_bar_city);
        mDistrictProgressBar = (ProgressBar) findViewById(R.id.progress_bar_district);
        mStreetProgressBar = (ProgressBar) findViewById(R.id.progress_bar_street);
    }

    private void setUserProfileInfo() {
        final User currentUser = App.dataManager.getCurrentUser();
        if (currentUser != null && !TextUtils.isEmpty(App.spManager.getApiKey())) {
            mDistrictTextView.setText(currentUser.getAddress().getDistrict().getTitle());
            mCityTextView.setText(currentUser.getAddress().getCity().getTitle());
            mStreetTextView.setText(currentUser.getAddress().getStreet().getName());
            mHouseTextView.setText(currentUser.getAddress().getHouse().getName());
            mFlatTextView.setText(currentUser.getAddress().getFlat());
            mStreetTextView.setSelection(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address, menu);
        mMenuDone = menu.findItem(R.id.done);
        mMenuDone.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                if (isFilledData()) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    if (ValidatorUtil.isValidFlat(mFlatTextView.getText().toString())) {
                        bundle.putString(FLAT, mFlatTextView.getText().toString());
                    } else {
                        mErrorHelper.setError(mFlatTextView, R.string.error_text_empty);
                        return false;
                    }
                    bundle.putLong(DISTRICT_ID, mDistrictId);
                    bundle.putLong(CITY_ID, mCityId);
                    bundle.putLong(STREET_ID, mStreetId);
                    bundle.putLong(HOUSE_ID, mHouseId);
                    intent.putExtra(BUNDLE_ID, bundle);
                    setResult(SIGN_UP_RESULT_CODE, intent);
                    finish();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isFilledData() {
        if (errorField(mDistrictTextView, mDistrictId, getString(R.string.error_choose_district))) {
            return false;
        }
        if (errorField(mCityTextView, mCityId, getString(R.string.error_choose_city))) {
            return false;
        }
        if (errorField(mStreetTextView, mStreetId, getString(R.string.error_choose_street))) {
            return false;
        }
        if (errorField(mHouseTextView, mHouseId, getString(R.string.error_choose_house))) {
            return false;
        }
        return true;
    }

    private boolean errorField(final AutoCompleteTextView autoCompleteTextView, final long id,
                               final String errorText) {
        if (id == 0) {
            autoCompleteTextView.setError(errorText);
            return true;
        }
        return false;
    }

    private List<BaseAddress> convertDistrictToBase(final RealmResults<District> realmResults) {
        List<BaseAddress> baseAddresses = new LinkedList<>();
        for (District address : realmResults) {
            BaseAddress baseAddress = new BaseAddress(address.getId(), address.getTitle(), "");
            baseAddresses.add(baseAddress);
        }
        Collections.sort(baseAddresses);
        return baseAddresses;
    }

    private List<BaseAddress> convertHouseToBase(final RealmResults<House> realmResults) {
        List<BaseAddress> baseAddresses = new LinkedList<>();
        for (House address : realmResults) {
            BaseAddress baseAddress = new BaseAddress(address.getId(), address.getName(), "");
            baseAddresses.add(baseAddress);
        }
        Collections.sort(baseAddresses, new Comparator<BaseAddress>() {
            @Override
            public int compare(final BaseAddress lhs, final BaseAddress rhs) {
                return new AlphanumComparator().compare(lhs.getTitle(), rhs.getTitle());

            }
        });
        return baseAddresses;
    }

    private List<BaseAddress> convertCityToBase(final RealmResults<City> realmResults) {
        List<BaseAddress> baseAddresses = new LinkedList<>();
        for (City address : realmResults) {
            BaseAddress baseAddress = new BaseAddress(address.getId(), address.getTitle(), address.getNameRu());
            baseAddresses.add(baseAddress);
        }
        Collections.sort(baseAddresses);
        return baseAddresses;
    }

    private List<BaseAddress> convertStreetToBase(final RealmResults<Street> realmResults) {
        List<BaseAddress> baseAddresses = new LinkedList<>();
        for (Street address : realmResults) {
            String streetName = getStreetName(address.getName(), address.getStreetType(), address.getCityDistrict());
            String streetNameRu = "";
            if (!TextUtils.isEmpty(address.getNameRu())) {
                streetNameRu = getStreetName(address.getNameRu(), address.getStreetType(), address.getCityDistrict());
            }
            baseAddresses.add(new BaseAddress(address.getId(), streetName, streetNameRu));
        }
        Collections.sort(baseAddresses);
        return baseAddresses;
    }

    private void callAddressDialog(final List<BaseAddress> baseAddresses,
                                   final IAddressChosen iAddressChosen, final String title,
                                   final long chosenId) {
        String[] arrString = new String[baseAddresses.size()];
        int selectedId = -1;

        for (int i = 0; i < baseAddresses.size(); i++) {
            arrString[i] = baseAddresses.get(i).getTitle();
            if (baseAddresses.get(i).getId() == chosenId) {
                selectedId = i;
            }
        }
        new MaterialDialog.Builder(this)
                .title(title)
                .items(arrString)
                .itemsCallbackSingleChoice(selectedId, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(final MaterialDialog dialog, final View view,
                                               final int which, final CharSequence text) {
                        if (which >= 0 && baseAddresses.size() > which) {
                            iAddressChosen.onItemClick(baseAddresses.get(which));
                        }
                        return true;
                    }
                })
                .positiveText(R.string.dialog_category_ok)
                .show();
    }

    private String getStreetName(final String name, final StreetType streetType,
                                 final CityDistrict cityDistrict) {
        StringBuilder streetName = new StringBuilder();
        if (streetType != null && !TextUtils.isEmpty(streetType.getShortName())) {
            streetName.append(streetType.getShortName()).append(" ");
        }
        streetName.append(name);
        if (cityDistrict != null && !TextUtils.isEmpty(cityDistrict.getTitle())) {
            streetName.append(" (").append(cityDistrict.getTitle()).append(")");
        }
        return streetName.toString();
    }


    /**
     * Address selection interface
     */
    private interface IAddressChosen {
        void onItemClick(BaseAddress baseAddress);
    }
}
