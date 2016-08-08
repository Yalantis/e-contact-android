package ua.gov.dp.econtact.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.realm.RealmList;
import timber.log.Timber;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.auth.AddressActivity;
import ua.gov.dp.econtact.adapter.CategoriesRadioAdapter;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.event.tickets.TicketCreatedEvent;
import ua.gov.dp.econtact.event.tickets.TicketImageErrorEvent;
import ua.gov.dp.econtact.event.tickets.TicketImageEvent;
import ua.gov.dp.econtact.fragment.dialog.ImageSourcePickerFragment;
import ua.gov.dp.econtact.gallery.androidcustomgallery.GalleryActivity;
import ua.gov.dp.econtact.gallery.databinders.DataHolder;
import ua.gov.dp.econtact.model.GeoAddress;
import ua.gov.dp.econtact.model.State;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketFiles;
import ua.gov.dp.econtact.model.Type;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.address.City;
import ua.gov.dp.econtact.model.address.District;
import ua.gov.dp.econtact.model.address.House;
import ua.gov.dp.econtact.model.address.Street;
import ua.gov.dp.econtact.model.category.Category;
import ua.gov.dp.econtact.model.category.CategoryWithImages;
import ua.gov.dp.econtact.model.dto.CreateTicketDTO;
import ua.gov.dp.econtact.util.Connectivity;
import ua.gov.dp.econtact.util.FileUtil;
import ua.gov.dp.econtact.util.IdGeneratorUtil;
import ua.gov.dp.econtact.util.KeyboardUtils;
import ua.gov.dp.econtact.util.Toaster;
import ua.gov.dp.econtact.view.ImageEditItem;

public class NewTicketActivity extends BaseActivity implements ImageSourcePickerFragment.ImageSourceListener {

    public static final String PHOTO = "photo";

    private static final int REQUEST_PICK_IMAGE = 54;
    private static final int REQUEST_CAMERA = 55;
    private static final int REQUEST_CROPPER = 56;
    private static final int REQUEST_PLACE_PICKER = 57;
    private static final int REQUEST_CODE = 101;
    private static final int TYPE_DEFAULT_ID = 5;
    private static final String URIS = "uris";
    private static final String PATHS = "paths";
    private static final String PHOTO_PATH = "photo_paths";

    @Bind(R.id.button_location_bind)
    Button mButtonConnectToMyAddress;
    @Bind(R.id.button_choose_address)
    Button mButtonChooseAddress;
    @Bind(R.id.territory_bind_switch)
    SwitchCompat mSwitchLocationConnect;
    @Bind(R.id.edit_text_problem)
    EditText mEditTextProblem;
    @Bind(R.id.container_image)
    LinearLayout mLinearLayoutTicketImage;
    @Bind(R.id.layout_choose_category)
    RelativeLayout mRelativeLayoutCategory;
    @Bind(R.id.txt_view_category)
    TextView mCategoryTextView;
    @Bind(R.id.address_text_view)
    TextView mAddressTextView;
    @Bind(R.id.address_layout)
    RelativeLayout mAddressLayout;

    private File mPhotoFile;
    private Type mType;
    private CategoryWithImages mCategory;
    private Address mAddress = new Address();
    private List<String> mPathsList;
    private Ticket mTicket;
    private GeoAddress mGeoAddress;
    private List<String> mCropPendingUriList;
    private MenuItem mSendItem;

    private int mCountImage;
    private long mTicketId;
    private String mPhotoPath;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);
        showBackButton();
        initData();
        fillUI();
    }

    private void fillUI() {
        hideButtons(mSwitchLocationConnect.isChecked());
        if (getIntent().getExtras() == null) {
            return;
        }
        // get mTicket object
        long draftTicketId = getIntent().getExtras().getLong(TicketActivity.ID);
        mTicket = App.dataManager.getTicketById(draftTicketId);
        // fill class fields
        if (mTicket.getCategory() != null) {
            mCategory = new CategoryWithImages(mTicket.getCategory().getId(), mTicket.getCategory().getName());
        }
        mAddress = mTicket.getAddress();
        mGeoAddress = mTicket.getGeoAddress();
        for (TicketFiles file : mTicket.getFiles()) {
            addNewImage(Uri.parse(file.getFilename()));
        }
        // fill UI
        fillCategory();
        mEditTextProblem.setText(mTicket.getBody());
        if (mAddress != null || mGeoAddress != null) {
            mSwitchLocationConnect.setChecked(true);
        }
        mAddressTextView.setText(
                mAddress == null && mGeoAddress != null
                        ? GeoAddress.generateAddressLabel(mGeoAddress)
                        : Address.generateAddressLabel(mAddress));
    }

    private void initData() {
        mType = new Type();
        mType.setId(TYPE_DEFAULT_ID);
        //default value
        mPathsList = new ArrayList<>();
        mCropPendingUriList = new ArrayList<>();
    }

    @OnCheckedChanged(R.id.territory_bind_switch)
    void onTerritoryBindChanged(boolean b) {
        hideButtons(b);
    }

    @OnClick(R.id.button_location_bind)
    void onLocationBindClick() {
        if (!mButtonConnectToMyAddress.isSelected()) {
            User user = App.dataManager.getCurrentUser();
            if (user != null) {
                mAddress = user.getAddress();
            }
            mAddressTextView.setText(Address.generateAddressLabel(mAddress));
            mButtonConnectToMyAddress.setSelected(true);
            mButtonChooseAddress.setSelected(false);
            mGeoAddress = null;
        } else {
            mButtonConnectToMyAddress.setSelected(false);
            mAddress = new Address();
            mAddressTextView.setText(R.string.address_field_label);
        }
    }

    @OnClick(R.id.button_choose_address)
    void onChooseAddressClick() {
        startPlacePicker();
    }

    @OnClick(R.id.btn_attach_image)
    void onAttachImageClick() {
        ImageSourcePickerFragment.newInstance().show(getSupportFragmentManager());
    }

    @OnClick(R.id.layout_choose_category)
    void onChoseCategoryClick() {
        mRelativeLayoutCategory.setEnabled(false);
        callTypeDialog();
    }

    @OnClick(R.id.address_layout)
    void onAddressLayoutClick() {
        if (mGeoAddress == null) {
            Intent intent = AddressActivity.newInstance(NewTicketActivity.this, true);
            startActivityForResult(intent, AddressActivity.SIGN_UP_REQUEST_CODE);
        } else {
            startPlacePicker();
        }
    }


    private void hideButtons(final boolean show) {
        mButtonChooseAddress.setVisibility(show ? View.VISIBLE : View.GONE);
        mButtonConnectToMyAddress.setVisibility(show ? View.VISIBLE : View.GONE);
        mAddressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        if (!show) {
            mAddressTextView.setText(R.string.address_field_label);
            mAddress = null;
            mGeoAddress = null;
            mButtonChooseAddress.setSelected(false);
            mButtonConnectToMyAddress.setSelected(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_send:
                mSendItem.setEnabled(false);
                sendAnIssue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void sendAnIssue() {
        KeyboardUtils.hide(this);
        if (Connectivity.isDeviceOnline(this)) {
            //APi request
            if (isValidate()) {
                // if there is mTicket in drafts - delete it
                if (mTicket != null) {
                    App.dataManager.deleteTicketById(mTicket.getId());
                }
                // create new mTicket
                CreateTicketDTO createTicketDTO = new CreateTicketDTO();
                createTicketDTO.setTicketId("");
                createTicketDTO.setBody(mEditTextProblem.getText().toString());
                createTicketDTO.setTitle(mCategory.getName());
                createTicketDTO.setCreated(Calendar.getInstance().getTimeInMillis());
                createTicketDTO.setType(mType);
                createTicketDTO.setCategory(new Category(mCategory.getId(), mCategory.getName()));
                createTicketDTO.setUser(App.dataManager.getCurrentUser());
                State state = new State();
                state.setId(Const.TICKET_STATUS_MODERATION);
                createTicketDTO.setState(state);
                if (mAddress != null) {
                    createTicketDTO.setAddress(mAddress);
                    GeoAddress geoAddress = new GeoAddress();
                    geoAddress.setId(IdGeneratorUtil.getGeoAddressId());
                    geoAddress.setAddress(Address.generateAddressLabelWithoutFlat(mAddress));
                    createTicketDTO.setGeoAddress(geoAddress);
                } else if (mGeoAddress != null) {
                    createTicketDTO.setGeoAddress(mGeoAddress);
                }
                showProgress(false);
                App.apiManager.createTicket(createTicketDTO);
            } else {
                enableSendMenuItem();
            }
        } else if (isSomethingToSave()) {
            showDraftDialog(R.string.dialog_draft_connectivity_message);
            enableSendMenuItem();
        }
    }

    private void enableSendMenuItem() {
        mSendItem.setEnabled(true);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_ticket, menu);
        mSendItem = menu.findItem(R.id.action_send);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onTake() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            mPhotoFile = null;
            try {
                mPhotoFile = FileUtil.createImageFile(this);
                mPhotoPath = mPhotoFile.getAbsolutePath();
            } catch (IOException e) {
                Timber.e(e, "Failed creating image file");
            }
            if (mPhotoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onChooseExisting() {
        GalleryActivity.navigateToGallery(this, REQUEST_CODE, Const.GalleryType.IMAGE, true);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {

            switch (requestCode) {
                case REQUEST_CODE:
                    ArrayList<DataHolder> list = data.getParcelableArrayListExtra(Const.IntentConstant.LIST_OF_PATHS);
                    for (DataHolder dataHolder : list) {
                        Uri uri;
                        try {
                            uri = Uri.fromFile(new File(dataHolder.getCropPath()));
                        } catch (NullPointerException e) {
                            uri = Uri.parse(dataHolder.getCropPath());
                        }
                        mCropPendingUriList.add(uri.toString());
                    }
                    if (!mCropPendingUriList.isEmpty()) {
                        openCropper(Uri.parse(mCropPendingUriList.get(0)));
                        mCropPendingUriList.remove(0);
                    }
                    break;
                case REQUEST_CAMERA:
                    try {
                        if (mPhotoFile == null) {
                            mPhotoFile = new File(mPhotoPath);
                        }
                        openCropper(Uri.fromFile(mPhotoFile));
                    } catch (NullPointerException e) {
                        Toaster.share(mToolbar, R.string.error);
                    }
                    break;
                case REQUEST_PICK_IMAGE:
                    openCropper(data.getData());
                    break;
                case REQUEST_CROPPER:
                    if (!data.getStringExtra(PhotoCropActivity.KEY_PHOTO_PATH).isEmpty()) {
                        addNewImage(Uri.parse(data.getStringExtra(PhotoCropActivity.KEY_PHOTO_PATH)));
                    }
                    if (!mCropPendingUriList.isEmpty()) {
                        openCropper(Uri.parse(mCropPendingUriList.get(0)));
                        mCropPendingUriList.remove(0);
                    }
                    break;
                case REQUEST_PLACE_PICKER:
                    // The user has selected a place. Extract the name and address.
                    final Place place = PlacePicker.getPlace(data, this);

                    CharSequence address = place.getAddress();
                    long oldGeoAddressId = -1;
                    if (mGeoAddress != null) {
                        oldGeoAddressId = mGeoAddress.getId();
                    }
                    mGeoAddress = new GeoAddress();
                    mGeoAddress.setId(oldGeoAddressId == -1 ? IdGeneratorUtil.getGeoAddressId() : oldGeoAddressId);
                    mGeoAddress.setLatitude(String.valueOf(place.getLatLng().latitude));
                    mGeoAddress.setLongitude(String.valueOf(place.getLatLng().longitude));
                    mGeoAddress.setAddress(address.toString());
                    mAddressTextView.setText(GeoAddress.generateAddressLabel(mGeoAddress));

                    mAddress = null;
                    mButtonConnectToMyAddress.setSelected(false);
                    mButtonChooseAddress.setSelected(true);
                    break;
                default:
                    break;
            }
        }
        if (requestCode == AddressActivity.SIGN_UP_REQUEST_CODE && resultCode == AddressActivity.SIGN_UP_RESULT_CODE) {
            mGeoAddress = null;
            mButtonConnectToMyAddress.setSelected(false);
            fillAddress(data);
        }
    }

    private void openCropper(final Uri uri) {
        startActivityForResult(new Intent(this, PhotoCropActivity.class).putExtra(PHOTO, uri.toString()), REQUEST_CROPPER);
    }

    private void addNewImage(final Uri uri) {
        mPathsList.add(uri.getPath());
        ImageEditItem imageEditItem = new ImageEditItem(this);
        mLinearLayoutTicketImage.addView(imageEditItem);
        imageEditItem.bindData(uri, new ImageEditItem.OnItemDeleteListener() {
            @Override
            public void onItemDelete(final ImageEditItem imageEditItem, final String path) {
                mLinearLayoutTicketImage.removeView(imageEditItem);
                mPathsList.remove(path);
            }
        });
        imageEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                navigateToImageScreen(NewTicketActivity.this, mPathsList, mPathsList.indexOf(uri.getPath()));
            }
        });
    }

    private void navigateToImageScreen(final Activity activity, final List<String> paths, final int position) {
        final Intent intent = new Intent(activity, ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.EXTRA_PATHS, new ArrayList<>(paths));
        intent.putExtra(ImageViewActivity.EXTRA_ITEM, position);
        activity.startActivity(intent);
    }

    public void onEvent(final TicketCreatedEvent event) {
        mTicketId = event.getData().getId();
        removeStickyEvent(event);
        if (mPathsList.isEmpty()) {
            hideProgress();
            startActivity(MainActivity.newInstance(NewTicketActivity.this, true));
            finish();
        } else {
            for (String path : mPathsList) {
                App.apiManager.uploadPhoto(mTicketId, path);
            }
        }
    }

    public void onEvent(final TicketImageEvent event) {
        if (mTicketId != 0 && mTicketId == event.getTicketId()) {
            mCountImage++;
            if (mCountImage == mPathsList.size()) {
                removeStickyEvent(event);
                hideProgress();
                startActivity(MainActivity.newInstance(NewTicketActivity.this, true));
                finish();
            }
        }
    }


    public void onEvent(final TicketImageErrorEvent event) {
        hideProgress();
        enableSendMenuItem();
    }


    public void onEvent(final ErrorApiEvent event) {
        hideProgress();
        enableSendMenuItem();
        Toaster.share(mEditTextProblem, event.getMessage(),
                getResources().getColor(R.color.white_80_transparent), Color.BLACK);
    }

    private void callTypeDialog() {
        // get categories
        final List<CategoryWithImages> categoryList = new ArrayList<>(App.dataManager.getAllCategories());
        // sort
        final Collator uaCollator = Collator.getInstance(new Locale("uk", "UA"));
        Collections.sort(categoryList, new Comparator<CategoryWithImages>() {
            @Override
            public int compare(final CategoryWithImages lhs, final CategoryWithImages rhs) {
                return lhs.getName() == null
                        ? -1 : rhs.getName() == null
                        ? 1 : uaCollator.compare(lhs.getName(), rhs.getName());
            }
        });
        // create list for adapter
        List<CategoriesRadioAdapter.AdapterItem> categories = new ArrayList<>();
        for (CategoryWithImages category : categoryList) {
            categories.add(new CategoriesRadioAdapter.AdapterItem(category, false));
        }

        final CategoriesRadioAdapter adapter = new CategoriesRadioAdapter(this, categories);
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_category_title)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog materialDialog, final View view,
                                            final int i, final CharSequence charSequence) {
                        adapter.select(i);
                    }
                })
                .positiveText(R.string.dialog_category_ok)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        int selection = adapter.getSelection();
                        if (selection != -1) {
                            mCategory = categoryList.get(selection);
                            fillCategory();
                            mRelativeLayoutCategory.setEnabled(true);
                        }
                    }

                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface dialog) {
                        mRelativeLayoutCategory.setEnabled(true);
                    }
                })
                .show();
    }

    private void fillCategory() {
        if (mCategory != null) {
            mCategoryTextView.setText(mCategory.getName());
        }
    }

    private void fillAddress(final Intent data) {
        mAddress = new Address();
        Bundle bundle = data.getBundleExtra(AddressActivity.BUNDLE_ID);
        final long districtId = bundle.getLong(AddressActivity.DISTRICT_ID, 0);
        final long cityId = bundle.getLong(AddressActivity.CITY_ID, 0);
        final long streetId = bundle.getLong(AddressActivity.STREET_ID, 0);
        final long houseId = bundle.getLong(AddressActivity.HOUSE_ID, 0);
        final String flat = bundle.getString(AddressActivity.FLAT);
        final District district = App.dataManager.getDistrictById(districtId);
        final City city = App.dataManager.getCityById(cityId);
        final Street street = App.dataManager.getStreetById(streetId);
        final House house = App.dataManager.getHouseById(houseId);
        mAddress.setCity(city);
        mAddress.setDistrict(district);
        mAddress.setFlat(flat);
        mAddress.setHouse(house);
        mAddress.setStreet(street);
        mAddressTextView.setText(Address.generateAddressLabel(mAddress));
    }

    public boolean isValidate() {
        if (mCategory == null) {
            Toaster.share(mToolbar, R.string.error_choose_category);
            return false;
        }

        if (TextUtils.isEmpty(mEditTextProblem.getText().toString().trim())) {
            Toaster.share(mToolbar, R.string.error_choose_problem);
            return false;
        }

        if (mSwitchLocationConnect.isChecked() && mAddress == null && mGeoAddress == null) {
            Toaster.share(mToolbar, R.string.error_choose_address);
            return false;
        }
        return true;
    }

    private boolean isSomethingToSave() {
        return mCategory != null || mAddress != null && mAddress.getDistrict() != null
                || !TextUtils.isEmpty(mEditTextProblem.getText().toString())
                || !mPathsList.isEmpty() || mGeoAddress != null;
    }

    private void saveDraft() {
        int millisDivider = 1000;
        Ticket ticket = new Ticket();
        ticket.setId(mTicket == null ? IdGeneratorUtil.getDraftTicketId() : mTicket.getId());
        ticket.setCreated(System.currentTimeMillis() / millisDivider);
        if (mCategory != null) {
            ticket.setTitle(mCategory.getName());
            ticket.setCategory(new Category(mCategory.getId(), mCategory.getName()));
        }

        if (mAddress != null) {
            Address address = new Address();
            address.setCity(mAddress.getCity());
            address.setDistrict(mAddress.getDistrict());
            address.setFlat(mAddress.getFlat());
            address.setHouse(mAddress.getHouse());
            address.setStreet(mAddress.getStreet());
            address.setId(IdGeneratorUtil.getDraftAddressId());
            ticket.setAddress(address);
        }

        ticket.setBody(mEditTextProblem.getText().toString());
        ticket.setUser(App.dataManager.getCurrentUser());
        RealmList<TicketFiles> files = new RealmList<>();
        long firstFileId = IdGeneratorUtil.getDraftFileId();
        for (String path : mPathsList) {
            TicketFiles file = new TicketFiles();
            file.setId(firstFileId++);
            file.setFilename(path);
            files.add(file);
        }
        ticket.setGeoAddress(mGeoAddress);
        ticket.setFiles(files);
        State state = new State();
        state.setId(Const.TICKET_STATUS_DRAFT);
        ticket.setState(state);
        App.dataManager.saveTicketsToDB(new ArrayList<>(Collections.singletonList(ticket)));
        setResult(RESULT_OK);
    }

    private void showDraftDialog(final int messageStrId) {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_draft_title)
                .content(messageStrId)
                .positiveText(R.string.dialog_confirm_exit_positive)
                .negativeText(R.string.dialog_confirm_exit_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        super.onPositive(dialog);
                        saveDraft();
                        NewTicketActivity.super.onBackPressed();
                    }

                    @Override
                    public void onNegative(final MaterialDialog dialog) {
                        super.onNegative(dialog);
                        NewTicketActivity.super.onBackPressed();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        if (isSomethingToSave()) {
            showDraftDialog(isDraftAlready()
                    ? R.string.dialog_draft_changes_message : R.string.dialog_draft_back_message);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isDraftAlready() {
        return mTicket != null;
    }

    private void startPlacePicker() {
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();

            Intent intent = intentBuilder.build(this);

            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Toaster.showShort(this, R.string.device_is_not_supported);
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mCropPendingUriList = savedInstanceState.getStringArrayList(URIS);
            mPhotoPath = savedInstanceState.getString(PHOTO_PATH);
            mPathsList = savedInstanceState.getStringArrayList(PATHS);
            addImages(mPathsList);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(URIS, (ArrayList<String>) mCropPendingUriList);
        outState.putStringArrayList(PATHS, (ArrayList<String>) mPathsList);
        outState.putString(PHOTO_PATH, mPhotoPath);
    }


    public void addImages(final List<String> list) {
        for (final String string : list) {
            ImageEditItem imageEditItem = new ImageEditItem(this);
            mLinearLayoutTicketImage.addView(imageEditItem);
            imageEditItem.bindData(Uri.parse(string), new ImageEditItem.OnItemDeleteListener() {
                @Override
                public void onItemDelete(final ImageEditItem imageEditItem, final String path) {
                    mLinearLayoutTicketImage.removeView(imageEditItem);
                    mPathsList.remove(path);
                }
            });
            imageEditItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    navigateToImageScreen(NewTicketActivity.this, mPathsList, mPathsList.indexOf(string));
                }
            });
        }
    }
}
