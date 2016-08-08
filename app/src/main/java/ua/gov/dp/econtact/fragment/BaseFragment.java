package ua.gov.dp.econtact.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import timber.log.Timber;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.event.BaseEvent;
import ua.gov.dp.econtact.interfaces.ChangeTitleListener;

/**
 * Created by Yalantis
 * 25.09.2014.
 *
 * @author Dmitriy Dovbnya
 */
public abstract class BaseFragment extends Fragment {

    public static final int LOCATION_REQUEST_CODE = 101;

    protected Activity mActivity;
    protected Handler mHandler;
    protected View mView;

    protected ChangeTitleListener mChangeTitleListener;

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            mChangeTitleListener = (ChangeTitleListener) activity;
        } catch (ClassCastException e) {
            Timber.e("%1s should implement %2s ", activity.getClass().getSimpleName(), ChangeTitleListener.class.getSimpleName());
        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this,mView);
        return mView;
    }

    protected abstract int getLayoutId();

    @Override
    public void onStart() {
        super.onStart();
        App.eventBus.registerSticky(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        App.eventBus.unregister(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void onEvent(final BaseEvent event) {
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

    protected boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    protected void showLocationPermissionSnackbar(final CoordinatorLayout coordinatorLayout) {
        Snackbar.make(coordinatorLayout, R.string.label_enable_location_on_map,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.label_enable, new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }).show();
    }
}
