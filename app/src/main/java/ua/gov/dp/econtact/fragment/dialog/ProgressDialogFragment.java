package ua.gov.dp.econtact.fragment.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ua.gov.dp.econtact.R;

/**
 * Created by Yalantis
 * 01.12.2014.
 *
 * @author Aleksandr
 */
public class ProgressDialogFragment extends BaseDialogFragment {

    private DialogInterface.OnCancelListener mCancelListener;

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }

    public String getFragmentTag() {
        return ProgressDialogFragment.class.getSimpleName();
    }

    public void setOnCancelListener(final DialogInterface.OnCancelListener listener) {
        this.mCancelListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.dlg_wait));
        return dialog;
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        super.onCancel(dialog);
        if (mCancelListener != null) {
            mCancelListener.onCancel(dialog);
        }
    }
}
