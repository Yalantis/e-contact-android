package ua.gov.dp.econtact.gallery.customcomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import ua.gov.dp.econtact.R;


/**
 * GalleryFrameLayout - custom frameLayout for handling
 * gallery item click and resizing according to gridView cell size
 *
 * @author Ed Baev
 */
public class GalleryFrameLayout extends FrameLayout {

    public static final int UNCHECK = 0;
    public static final int CHECK = 1;
    public static final int LOW = 2;

    //selected state
    private int mChecked;

    public GalleryFrameLayout(final Context context) {
        super(context);
    }

    public GalleryFrameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryFrameLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY));
    }

    /**
     * function for setting selected state of view
     *
     * @param checked boolean value
     */
    public void setChecked(final int checked) {
        mChecked = checked;
        switch (checked) {
            case 0:
                setForeground(getResources().getDrawable(R.drawable.gridview_selector));
                break;
            case 1:
                setForeground(getResources().getDrawable(R.drawable.selected_photo));
                break;
            case 2:
                setForeground(getResources().getDrawable(R.drawable.low_resolution));
                break;
            default:
                break;
        }
    }

    /**
     * @return boolean value indicating checked state
     */
    public int getChecked() {
        return mChecked;
    }

}
