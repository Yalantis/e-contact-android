package ua.gov.dp.econtact.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Yalantis
 *
 * @author Andrew Khristyan
 */
final class SquaredImageView extends ImageView {

    public SquaredImageView(final Context context) {
        super(context);
    }

    public SquaredImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
