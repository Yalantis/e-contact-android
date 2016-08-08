package ua.gov.dp.econtact.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.auth.LoginActivity;
import ua.gov.dp.econtact.adapter.CategoriesAdapter;
import ua.gov.dp.econtact.event.LogoutEvent;
import ua.gov.dp.econtact.fragment.MapFragment;
import ua.gov.dp.econtact.fragment.ticket.TicketsFragment;
import ua.gov.dp.econtact.fragment.ticket.TicketsListFragment;
import ua.gov.dp.econtact.interfaces.TicketListFragmentListener;
import ua.gov.dp.econtact.listeners.MapFragmentListener;
import ua.gov.dp.econtact.listeners.SimpleDrawerListener;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.push.RegistrationService;
import ua.gov.dp.econtact.util.AccountUtil;
import ua.gov.dp.econtact.util.CategoryUtils;
import ua.gov.dp.econtact.util.ScrollAwareFABBehavior;
import ua.gov.dp.econtact.util.Toaster;
import ua.gov.dp.econtact.view.ClickableUrlSpan;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        TicketListFragmentListener, MapFragmentListener {

    public static final int REQUEST_NEW_TICKET = 21;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int DELAY_MILLIS = 250;
    private static final String KEY_ITEM_SELECTED = "ITEM_SELECTED";
    private static final String KEY_NEW_TICKET = "NEW_TICKET";

    @Bind(R.id.navigation_view)
    NavigationView mDrawer;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.text_view_author)
    TextView mTextAuthor;
    @Bind(R.id.btn_add_ticket)
    FloatingActionButton mButtonAddTicket;
    @Bind(R.id.toolbar_shadow)
    View mToolbarShadow;
    @Bind(R.id.coordinator_main)
    CoordinatorLayout mCoordinatorLayout;

    private MaterialDialog mFilterDialog;
    private Fragment mCurrentFragment;

    private boolean isUserSignedIn;
    private int mSelectedMenuItemId;
    private int mTitleId;

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            showFab();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    public static Intent newInstance(final Context context, final boolean isNewTicket) {
        Intent intent = new Intent(context, MainActivity.class);
        if (isNewTicket) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_NEW_TICKET, true);
        }
        return intent;
    }

    @Override
    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setGradientLayout(mCoordinatorLayout);
        setAuthorLinks();
        showFab();

        final boolean isNewTicket = getIntent().getBooleanExtra(KEY_NEW_TICKET, false);
        supportInvalidateOptionsMenu();
        if (isNewTicket) {
            Toaster.share(mToolbar, R.string.ticket_created,
                    getResources().getColor(R.color.black_80_transparent), Color.WHITE);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        isUserSignedIn = AccountUtil.isLoggedIn(this);
        initDrawer();

        configureToolbarShadow();
        registerGcm();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isUserSignedIn != AccountUtil.isLoggedIn(this)) {
            isUserSignedIn = AccountUtil.isLoggedIn(this);
            mDrawer.getMenu().clear();
            mDrawer.inflateMenu(isUserSignedIn ? R.menu.main_activity_drawer_full : R.menu.main_activity_drawer_short);
            if (mSelectedMenuItemId != 0) {
                mDrawer.getMenu().performIdentifierAction(mSelectedMenuItemId, 0);
            }
        }
    }

    private void initDrawer() {
        mDrawer.getMenu().clear();
        mDrawerLayout.addDrawerListener(new SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(final View drawerView) {
                if (mSelectedMenuItemId != R.id.menu_statistic && mSelectedMenuItemId != R.id.tickets_on_map) {
                    showFab();
                }
            }
        });

        mDrawer.inflateMenu(isUserSignedIn ? R.menu.main_activity_drawer_full : R.menu.main_activity_drawer_short);

        mDrawer.setNavigationItemSelectedListener(this);
        mDrawer.getMenu().performIdentifierAction(R.id.all_tickets, 0);
    }


    @OnClick(R.id.btn_add_ticket)
    void onClickAddTicket() {
        if (TextUtils.isEmpty(App.accountManager.getAuthToken())) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            startActivityForResult(new Intent(MainActivity.this, NewTicketActivity.class),
                    REQUEST_NEW_TICKET);
        }
    }

    private void configureToolbarShadow() {
        mToolbarShadow.setVisibility(mCurrentFragment instanceof TicketsFragment
                ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.all_tickets:
                        mTitleId = R.string.all_tickets;
                        mCurrentFragment = TicketsFragment.newInstance(false, mOnPageChangeListener);
                        break;
                    case R.id.tickets_on_map:
                        mTitleId = R.string.tickets_on_map;
                        mCurrentFragment = new MapFragment();
                        break;
                    case R.id.my_tickets:
                        mCurrentFragment = TicketsListFragment.newInstance(TicketStates.MY_TICKET);
                        mTitleId = R.string.my_tickets;
                        break;
                    case R.id.drafts:
                        mCurrentFragment = TicketsListFragment.newInstance(TicketStates.DRAFT);
                        mTitleId = R.string.drafts;
                        break;
                    case R.id.profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                    case R.id.sign_in:
                        goToLoginScreen();
                        break;
                    case R.id.sign_out:
                        signOutMenuItem();
                        if (mSelectedMenuItemId == R.id.my_tickets || mSelectedMenuItemId == R.id.drafts) {
                            mSelectedMenuItemId = R.id.all_tickets;
                            mTitleId = R.string.all_tickets;
                            mCurrentFragment = TicketsFragment.newInstance(false, mOnPageChangeListener);
                        }
                        break;
                    default:
                        break;
                }

                if (mCurrentFragment != null) {
                    menuItem.setChecked(true);
                    performScreenChange(menuItem.getItemId());
                    setTitle(mTitleId);
                }
            }
        }, mSelectedMenuItemId == 0 ? 0 : DELAY_MILLIS);

        mDrawerLayout.closeDrawers();
        return true;
    }

    private void showFab() {
        mButtonAddTicket.show();
    }

    private void signOutMenuItem() {
        App.apiManager.logout();
        showProgress();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tickets, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setVisible(mSelectedMenuItemId != R.id.my_tickets && mSelectedMenuItemId != R.id.drafts && mSelectedMenuItemId != R.id.tickets_on_map);
        filterItem.setIcon(CategoryUtils.isAllSelected(this) ? R.drawable.ic_filter_menu : R.drawable.ic_filter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_filter:
                if (mFilterDialog == null || !mFilterDialog.isShowing()) {
                    callFilterDialog();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_NEW_TICKET && resultCode == RESULT_OK) {
            if (mCurrentFragment instanceof TicketsListFragment) {
                ((TicketsListFragment) mCurrentFragment).updateList();
            } else if (mCurrentFragment instanceof TicketsFragment) {
                ((TicketsFragment) mCurrentFragment).updateList();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void callFilterDialog() {
        final List<CategoriesAdapter.CategoryAdapterItem> categories = CategoryUtils.getCategories(this);
        final CategoriesAdapter adapter = new CategoriesAdapter(this, categories);

        mFilterDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_category_title)
                .adapter(adapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog materialDialog, final View view,
                                            final int i, final CharSequence charSequence) {
                        adapter.check(view, i);
                    }
                })
                .positiveText(R.string.dialog_category_ok)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        CategoryUtils.setCategories(MainActivity.this, adapter.getList());
                        supportInvalidateOptionsMenu();
                    }
                }).show();
    }

    private void goToLoginScreen() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void performScreenChange(final int menuItemId) {
        if (menuItemId != mSelectedMenuItemId) {
            final boolean scrollBehavior = menuItemId != 0;
            ScrollAwareFABBehavior.setReactOnScroll(scrollBehavior);
            if (menuItemId != 0) {
                mSelectedMenuItemId = menuItemId;
            }

            replaceFragment(mCurrentFragment, false, R.id.frame_content_main);
            configureToolbarShadow();
        }
    }

    @Override
    public ActionMode startActionMode(final TicketsListFragment.ActionModeCallback callback) {
        return startSupportActionMode(callback);
    }

    @Override
    public void showActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    @Override
    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Changes ui when logout executed
     */
    @Override
    public void logoutWithoutApi() {
        if ((mSelectedMenuItemId == R.id.my_tickets || mSelectedMenuItemId == R.id.drafts)) {
            mSelectedMenuItemId = R.id.all_tickets;
            mTitleId = R.string.all_tickets;
            mCurrentFragment = TicketsFragment.newInstance(false, mOnPageChangeListener);
            performScreenChange(mSelectedMenuItemId);
            setTitle(mTitleId);
        }
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            MenuItem myTicketsItem = mDrawer.getMenu() == null
                    ? null : mDrawer.getMenu().findItem(savedInstanceState.getInt(KEY_ITEM_SELECTED));

            if (myTicketsItem != null) {
                onNavigationItemSelected(myTicketsItem);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ITEM_SELECTED, mSelectedMenuItemId);
    }

    public void onEvent(LogoutEvent event) {
        removeStickyEvent(event);
        hideProgress();
        isUserSignedIn = false;
        initDrawer();
    }

    private void setAuthorLinks() {
        String yalantis = getString(R.string.yalantis);
        String itRuh = getString(R.string.it_ruh);
        String author = getString(R.string.author, itRuh, yalantis);
        SpannableString spannableAuthor = new SpannableString(author);

        int startYalantis = author.indexOf(yalantis);
        int endYalantis = startYalantis + yalantis.length();

        // Use new method available for M version

        spannableAuthor.setSpan(new ClickableUrlSpan(getString(R.string.yalantis_link)), startYalantis, endYalantis, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAuthor.setSpan(new UnderlineSpan(), startYalantis, endYalantis, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAuthor.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_link)), startYalantis, endYalantis, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int startRuh = author.indexOf(itRuh);
        int endRuh = startRuh + itRuh.length();
        spannableAuthor.setSpan(new ClickableUrlSpan(getString(R.string.it_ruh_link)), startRuh, endRuh, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAuthor.setSpan(new UnderlineSpan(), startRuh, endRuh, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAuthor.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_link)), startRuh, endRuh, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTextAuthor.setText(spannableAuthor);
        mTextAuthor.setMovementMethod(LinkMovementMethod.getInstance());
        mTextAuthor.setHighlightColor(Color.TRANSPARENT);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean isPlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toaster.showShort(this, R.string.device_is_not_supported);
            }
            return false;
        }
        return true;
    }

    private void registerGcm() {
        if (isPlayServicesAvailable()) {
            Intent intent = new Intent(this, RegistrationService.class);
            startService(intent);
        }
    }
}
