package ua.gov.dp.econtact.view;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.listeners.SimpleTextWatcher;

/**
 * As TextView (and EditText as a subclass)
 * can show error if it is in focus only
 * (many thanks to person who added this if condition to Android source code) -
 * we have to create own popup window and style it with custom styles
 * (internal styles are unavailable for developers).
 * This is helper to show / hide popup errors and drawable icons for TextView.
 */
public class ErrorPopupHelper {

    private ErrorPopup mErrorPopup;
    private TextView mLastTextView;
    private ImageView mLastImageView;

    /**
     * Hide error if user starts to write in this view
     */
    private TextWatcher textWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(@NonNull final CharSequence charSequence, final int i,
                                  final int i1, final int i2) {
            if (mErrorPopup != null && mErrorPopup.isShowing()) {
                cancel();
            }
        }
    };

    public void setError(final TextView mTextView, final int stringResId, ImageView imageView) {
        setError(mTextView, mTextView.getContext().getString(stringResId), imageView);
    }


    public void setError(final TextView mTextView, final int stringResId) {
        setError(mTextView, mTextView.getContext().getString(stringResId), null);
    }


    public void setError(final TextView mTextView, final CharSequence error, ImageView imageView) {
        cancel();

        CharSequence mError = TextUtils.stringOrSpannedString(error);
        if (mError != null) {
            showError(mTextView, error.toString(), imageView);
        }

        if (imageView == null) {
            mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    mError == null ? 0 : R.drawable.ic_error, 0);
        } else {
            imageView.setVisibility(mError == null ? View.INVISIBLE : View.VISIBLE);
        }

        mTextView.addTextChangedListener(textWatcher);

        mLastTextView = mTextView;
        mLastImageView = imageView;
    }

    public void cancel() {
        if (mLastTextView != null && mLastTextView.getWindowToken() != null) {
            hideError();
            mLastTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mLastTextView.removeTextChangedListener(textWatcher);
            mLastTextView = null;
            if (mLastImageView != null) {
                mLastImageView.setVisibility(View.INVISIBLE);
                mLastImageView = null;
            }
        }
    }

    private void showError(final TextView mTextView, final String error, ImageView imageView) {
        if (mTextView.getWindowToken() == null) {
            return;
        }

        if (mErrorPopup == null) {
            LayoutInflater inflater = LayoutInflater.from(mTextView.getContext());
            final TextView err = (TextView) inflater.inflate(R.layout.view_error_hint, null);
            mErrorPopup = new ErrorPopup(err, 0, 0);
            mErrorPopup.setFocusable(false);
            // The user is entering text, so the input method is needed.  We
            // don't want the popup to be displayed on top of it.
            mErrorPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        }

        Rect location = locateView(imageView);

        TextView tv = (TextView) mErrorPopup.getContentView();
        chooseSize(mErrorPopup, error, tv, imageView, mTextView);
        tv.setText(error);

        if (imageView == null) {
            mErrorPopup.showAsDropDown(mTextView, getErrorX(mTextView),
                    -mTextView.getContext().getResources().getDimensionPixelSize(R.dimen.textview_error_popup_margin));
            mErrorPopup.fixDirection(mErrorPopup.isAboveAnchor());
        } else {
            if (location != null) {
                mErrorPopup.showAtLocation(imageView, Gravity.TOP | Gravity.RIGHT, mTextView.getWidth() - imageView.getRight(), location.bottom);
            }
        }
    }

    private void hideError() {
        if (mErrorPopup != null) {
            if (mErrorPopup.isShowing()) {
                mErrorPopup.dismiss();
            }
            mErrorPopup = null;
        }
    }

    private Rect locateView(View v) {
        int[] locations = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(locations);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = locations[0];
        location.top = locations[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    private int getErrorX(final TextView mTextView) {
        return mTextView.getWidth() - mErrorPopup.getWidth(); // - mTextView.getPaddingRight();
    }

    private void chooseSize(final PopupWindow pop, final CharSequence text, final TextView tv, ImageView imageView, TextView mTextView) {
        final int wid = tv.getPaddingLeft() + tv.getPaddingRight();
        final int ht = tv.getPaddingTop() + tv.getPaddingBottom();

        int defaultWidthInPixels = tv.getResources().getDimensionPixelSize(
                R.dimen.textview_error_popup_default_width);
        Layout l = new StaticLayout(text, tv.getPaint(), defaultWidthInPixels,
                Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        float max = 0;
        for (int i = 0; i < l.getLineCount(); i++) {
            max = Math.max(max, l.getLineWidth(i));
        }

        /*
         * Now set the popup size to be big enough for the text plus the border capped
         * to DEFAULT_MAX_POPUP_WIDTH
         */
        int height = ht + l.getHeight();
        int width = wid + (int) Math.ceil(max);

        //adjusting width if it is bigger than screen can take
        if (imageView != null) {
            int popupWithOffset = width + (mTextView.getWidth() - imageView.getRight());
            if (popupWithOffset > App.getScreenWidth()) {
                pop.setWidth(width - (popupWithOffset - App.getScreenWidth()));
                pop.setHeight(height + popupWithOffset - App.getScreenWidth());
            }
        }else {
            pop.setWidth(width);
            pop.setHeight(height);
        }

    }
}
