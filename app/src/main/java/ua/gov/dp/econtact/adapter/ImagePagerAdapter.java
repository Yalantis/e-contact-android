package ua.gov.dp.econtact.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.view.ImageViewPager;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Yalantis
 * 11.09.2015.
 *
 * @author Aleksandr
 */
public class ImagePagerAdapter extends PagerAdapter {

    private List<Uri> mImagesUri;
    private ImageViewPager mViewPager;
    private Picasso mPicasso;

    public ImagePagerAdapter(final Context context, final List<Uri> images, final ImageViewPager viewPager) {
        final int cacheSize = 51200;
        mImagesUri = images;
        mViewPager = viewPager;
        mPicasso = new Picasso.Builder(context).memoryCache(new LruCache(cacheSize)).build();
    }

    @Override
    public int getCount() {
        return mImagesUri != null ? mImagesUri.size() : 0;
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        LinearLayout linearLayout = new LinearLayout(container.getContext());
        linearLayout.setGravity(Gravity.CENTER);
        final PhotoView photoView = new PhotoView(container.getContext());
        photoView.setId(R.id.photo_view);
        photoView.setBackgroundResource(R.drawable.placeholder);
        photoView.setOnScaleChangeListener(new PhotoViewAttacher.OnScaleChangeListener() {
            @Override
            public void onScaleChange(final float scaleFactor, final float focusX, final float focusY) {
                final float pagerLockScale = 1.1f;
                mViewPager.setLocked(photoView.getScale() > pagerLockScale);
            }
        });

        mPicasso.load(mImagesUri.get(position))
                .fit()
                .config(Bitmap.Config.RGB_565)
                .into(photoView);

        linearLayout.addView(photoView, App.getScreenWidth(), App.getScreenWidth());
        container.addView(linearLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return linearLayout;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        mPicasso.cancelRequest((ImageView) container.findViewById(R.id.photo_view));
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object;
    }
}
