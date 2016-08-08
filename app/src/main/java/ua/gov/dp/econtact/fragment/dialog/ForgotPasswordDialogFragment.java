package ua.gov.dp.econtact.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.interfaces.ForgotPasswordDialogListener;
import ua.gov.dp.econtact.util.Toaster;
import ua.gov.dp.econtact.util.ValidatorUtil;

public class ForgotPasswordDialogFragment extends BaseDialogFragment {

    @Bind(R.id.send_email_edit_text)
    EditText mEmail;
    private ForgotPasswordDialogListener mListener;

    public static ForgotPasswordDialogFragment newInstance() {
        return new ForgotPasswordDialogFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ForgotPasswordDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getLocalClassName() + " should implement " + ForgotPasswordDialogFragment.class.getSimpleName());
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_forget_password, null);
        builder.setView(rootView);
        ButterKnife.bind(this, rootView);
        return builder.create();
    }

    @OnClick(R.id.send_button)
    void onSendClick() {
        String email = mEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toaster.showShort(R.string.empty_email_message);
        } else if (!ValidatorUtil.isEmail(email)) {
            Toaster.showShort(R.string.email_validation_error);
        } else {
            mListener.resetPassword(email);
            dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public String getFragmentTag() {
        return null;
    }
}
