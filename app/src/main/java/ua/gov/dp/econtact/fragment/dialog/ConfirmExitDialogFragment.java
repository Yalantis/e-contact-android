package ua.gov.dp.econtact.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.interfaces.EditProfileActivityInterface;

/**
 * Created by Yalantis
 * 8/24/15.
 *
 * @author Artem Kholodnyi
 */
public class ConfirmExitDialogFragment extends BaseDialogFragment {

    private EditProfileActivityInterface mCallback;

    public static ConfirmExitDialogFragment newInstance() {
        return new ConfirmExitDialogFragment();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EditProfileActivityInterface) {
            mCallback = (EditProfileActivityInterface) activity;
        } else {
            throw new IllegalStateException("Should implement "
                    + EditProfileActivityInterface.class.getName());
        }
    }

    @Override
    public String getFragmentTag() {
        return ConfirmExitDialogFragment.class.getSimpleName();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_confirm_exit_message)
                .setPositiveButton(R.string.dialog_confirm_exit_positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                mCallback.saveChangesAndFinish();
                            }
                        }
                )
                .setNegativeButton(R.string.dialog_confirm_exit_negative,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int whichButton) {
                                getActivity().finish();
                            }
                        }
                )
                .create();
    }
}
