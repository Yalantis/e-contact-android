package ua.gov.dp.econtact.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.event.BaseEvent;
import ua.gov.dp.econtact.interfaces.AlertDialogListener;

import de.greenrobot.event.EventBus;

public abstract class BaseDialogFragment extends DialogFragment {

    protected Activity mActivity;
    protected AlertDialogListener mAlertListener;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(final BaseEvent event) {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        return dialog;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public abstract String getFragmentTag();

    public void show(final FragmentManager manager) {
        super.show(manager, getFragmentTag());
    }

    public void setAlertListener(final AlertDialogListener alertListener) {
        this.mAlertListener = alertListener;
    }
}
