package ua.gov.dp.econtact.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.net.HttpURLConnection;

import butterknife.ButterKnife;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.event.BaseEvent;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.interfaces.ChangeTitleListener;
import ua.gov.dp.econtact.util.BackgroundUtil;
import ua.gov.dp.econtact.util.BitmapFileUtil;
import ua.gov.dp.econtact.util.BlurImageUtil;
import ua.gov.dp.econtact.util.DisplayUtil;
import ua.gov.dp.econtact.util.VersionUtils;

/**
 * Created by Yalantis
 */
public abstract class BaseActivity extends AppCompatActivity implements ChangeTitleListener {

    protected Handler mHandler;
    protected Toolbar mToolbar;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void setContentView(final int layoutId) {
        super.setContentView(layoutId);
        ButterKnife.bind(this);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        // Activity may not have toolbar
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (VersionUtils.isAtLeastL()) {
                mToolbar.setElevation(0.0f);
            }
        }
    }

    protected void setGradientLayout(final ViewGroup viewGroup) {
        final float firstColorPosition = 0;
        final float secondColorPosition = 0.50f;
        final float thirdColorPosition = 0.70f;
        final float fourthColorPosition = 1;
        float[] positions = new float[]{firstColorPosition,
                secondColorPosition, thirdColorPosition, fourthColorPosition};
        int[] colors = getResources().getIntArray(R.array.gradient_colors);
        BackgroundUtil.setLayoutGradient(viewGroup, colors, positions);
    }

    public void setBlurredLandscape(final ViewGroup viewGroup, @DrawableRes final int bitmapRes) {
        if (VersionUtils.isAtJellyBeen()) {
            final File file = new File(getCacheDir(), Const.BLURRED_BACKGROUND_FILE_NAME);
            Bitmap result;
            if (!file.exists()) {
                Bitmap source = BitmapFactory.decodeResource(getResources(), bitmapRes);
                Point size = DisplayUtil.getDisplaySize();
                result = BlurImageUtil.blur(source, size.x, size.y);

                final Bitmap finalBitmap = result;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BitmapFileUtil.saveBitmapToFile(finalBitmap, file);
                    }
                }).run();
            } else {
                result = BitmapFactory.decodeFile(file.getPath());
            }

            if (null != result) {
                viewGroup.setBackground(new BitmapDrawable(getResources(), result));
            } else {
                setGradientLayout(viewGroup);
            }
        } else {
            setGradientLayout(viewGroup);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.eventBus.registerSticky(this);
    }

    public void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.eventBus.unregister(this);
    }

    public void showProgress() {
        showProgress(false);
    }

    protected void showProgress(final boolean cancellable) {
        if (mMaterialDialog == null) {
            mMaterialDialog = new MaterialDialog.Builder(this)
                    .customView(R.layout.dialog_progress, true)
                    .cancelable(cancellable).build();

            TextView contentView = (TextView) mMaterialDialog.getCustomView().findViewById(R.id.content);
            contentView.setText(R.string.loading_message);
        }
        mMaterialDialog.show();
    }

    protected boolean isLoading() {
        return mMaterialDialog != null && mMaterialDialog.isShowing();
    }

    public void hideProgress() {
        if (mMaterialDialog != null) {
            mMaterialDialog.cancel();
        }
    }

    public void removeStickyEvent(final Class<?> eventType) {
        final int delayMillis = 100;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                App.eventBus.removeStickyEvent(eventType);
            }
        }, delayMillis);
    }

    protected <T extends BaseEvent> void removeStickyEvent(final T event) {
        final int delayMillis = 100;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                App.eventBus.removeStickyEvent(event);
            }
        }, delayMillis);
    }

    protected void replaceFragment(final Fragment fragment, final boolean addToBackStack,
                                   final int containerId) {
        String backStateName = fragment.getClass().getName();
        if (getSupportFragmentManager().getFragments() != null) {
            Log.d("replaceFragment", getSupportFragmentManager().getFragments().size()
                    + " to string: " + getSupportFragmentManager().getFragments().toString());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(backStateName);
        }
        transaction.commit();
        invalidateOptionsMenu();
    }

    public void onEvent(final ErrorApiEvent event) {
        removeStickyEvent(event);
        if (event != null && event.getErrorResponse() != null && event.getErrorResponse().getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showProgress();
            App.clearSession();
        } else {
            hideProgress();
        }
    }

    @Override
    public void changeTitle(final String title) {
        if (!TextUtils.isEmpty(title) && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
