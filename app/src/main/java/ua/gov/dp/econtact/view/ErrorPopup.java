package ua.gov.dp.econtact.view;

import android.widget.PopupWindow;
import android.widget.TextView;

import ua.gov.dp.econtact.R;

/**
 * As TextView (and EditText as a subclass)
 * can show error if it is in focus only
 * (many thanks to person who added this if condition to Android source code) -
 * we have to create own popup window and style it with custom styles
 * (internal styles are unavailable for developers).
 * This is simple implementation of popup window.
 */
public class ErrorPopup extends PopupWindow {

    private final TextView mView;
    private boolean mAbove;

    public ErrorPopup(final TextView v, final int width, final int height) {
        super(v, width, height);
        mView = v;
        mView.setBackgroundResource(R.drawable.popup);
    }

    public void fixDirection(final boolean above) {
        mAbove = above;
        mView.setBackgroundResource(above ? R.drawable.popup_above : R.drawable.popup);
    }

    @Override
    public void update(final int x, final int y, final int w, final int h, final boolean force) {
        super.update(x, y, w, h, force);

        boolean above = isAboveAnchor();
        if (above != mAbove) {
            fixDirection(above);
        }
    }
}
