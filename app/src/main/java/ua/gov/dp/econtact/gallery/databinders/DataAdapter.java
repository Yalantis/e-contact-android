package ua.gov.dp.econtact.gallery.databinders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import ua.gov.dp.econtact.gallery.workers.CacheManager;
import ua.gov.dp.econtact.gallery.workers.GalleryLoader;

import java.util.concurrent.RejectedExecutionException;

/**
 * '
 * DataAdapter - custom BaseAdapter for populating
 * gallery
 *
 * @author Ed Baev
 */
public class DataAdapter extends BaseAdapter {

    //placeholder bitmap
    private static Bitmap mPlaceHolderBitmap;

    //current context
    private Context mContext;

    //cache for thumbnail images
    private CacheManager mCacheManager;

    //gallery type
    private int mGalleryType;

    //total number of items in view
    private int mItemCount;

    /**
     * constructor
     */
    public DataAdapter(final Context context, final int resource,
                       final int galleryType, final CacheManager cacheManager) {

        mContext = context;
        mGalleryType = galleryType;
        mCacheManager = cacheManager;
    }

    @Override
    public int getCount() {
        return mItemCount;
    }

    @Override
    public Object getItem(final int position) {
        return null;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        return null;
    }

    /**
     * function for getting current context
     *
     * @return current context
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * setting total items available in view
     *
     * @param itemCount
     */
    public void setItemCount(final int itemCount) {
        mItemCount = itemCount;
    }

    /**
     * function for getting gallery type
     *
     * @return gallery type
     */
    public int getGalleryType() {
        return mGalleryType;
    }


    /**
     * Load bitmaps from the cache if it is available in cache
     * otherwise process the bitmap and save it in cache
     *
     * @param type
     * @param resourceId
     * @param imageView
     */
    public boolean loadBitmap(final String type, final Long resourceId,
                              final ImageView imageView, final String path) {
        //unique key for thumbnail
        final String imageKey = String.valueOf(resourceId);

        //getting image from memory cache
        BitmapDrawable bitmap = mCacheManager.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageDrawable(bitmap);
            return true;
        }

        //processing bitmaps in separated thread
        if (GalleryLoader.cancelPotentialWork(resourceId, imageView)) {
            GalleryLoader galleryLoader = new GalleryLoader(mContext, imageView, type, mCacheManager, path);
            GalleryLoader.AsyncDrawable asyncDrawable = new GalleryLoader.AsyncDrawable(mContext.getResources(),
                    mPlaceHolderBitmap, galleryLoader);
            imageView.setImageDrawable(asyncDrawable);
            try {
                galleryLoader.execute(resourceId);

            } catch (RejectedExecutionException exception) {
                exception.printStackTrace();
                return false;
            }

        }
        return true;
    }
}
