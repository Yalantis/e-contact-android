package ua.gov.dp.econtact.gallery.workers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * GalleryLoader - Async class for loading gallery thumbnails
 *
 * @author Ed Baev
 */
public class GalleryLoader extends AsyncTask<Long, Object, Bitmap> {

    protected static BitmapDrawable mBitmapDrawable;

    protected WeakReference<ImageView> mImageReference;
    protected CacheManager mCacheManager;

    protected String mImageType;
    protected Context mContext;
    protected Long mResourceId = 0L;
    protected String mPath;

    /**
     * Constructor
     */
    public GalleryLoader(final Context context, final ImageView imageView, final String type,
                         final CacheManager cacheManager, final String path) {
        mCacheManager = cacheManager;
        mImageType = type;
        mContext = context;
        mPath = path;
        mImageReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(final Long... params) {
        mResourceId = params[0];

        boolean bitmapNotInDisk = false;
        mBitmapDrawable = mCacheManager.getBitmapFromDiskCache(String.valueOf(mResourceId));

        if (mBitmapDrawable == null) {
            bitmapNotInDisk = true;
            Bitmap resultBitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    mContext.getContentResolver(), mResourceId,
                    MediaStore.Images.Thumbnails.MINI_KIND, null);

            if (mPath == null) {
                mBitmapDrawable = new BitmapDrawable(mContext.getResources(), resultBitmap);
            } else if (resultBitmap != null) {
                mBitmapDrawable = getBitmap(resultBitmap, mPath);
            }
        }
        //adding created bitmap in cache
        mCacheManager.addBitmapToCache(String.valueOf(params[0]), mBitmapDrawable, bitmapNotInDisk);
        // Bitmap or null
        if (mBitmapDrawable != null) {
            return mBitmapDrawable.getBitmap();
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (isCancelled()) {
            result = null;
        }

        if (mImageReference != null && result != null) {
            final ImageView imageView = mImageReference.get();
            final GalleryLoader galleryLoader = getGalleryLoader(imageView);
            if (this == galleryLoader && imageView != null) {
                imageView.setImageBitmap(result);
            }
        }
        super.onPostExecute(result);
    }

    public static boolean cancelPotentialWork(final Long resourceId, final ImageView imageView) {
        final GalleryLoader galleryLoader = getGalleryLoader(imageView);

        if (galleryLoader != null) {
            final Long bitmapData = galleryLoader.mResourceId;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != resourceId) {
                // Cancel previous task
                galleryLoader.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static GalleryLoader getGalleryLoader(final ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getGalleryLoader();
            }
        }
        return null;
    }

    public BitmapDrawable getBitmap(Bitmap bitmap, final String path) {
        ExifInterface exif;
        Matrix matrix = new Matrix();
        final int ninetyDegrees = 90;
        int rotationAngle = 0;
        try {
            exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = ninetyDegrees;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = ninetyDegrees * 2;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationAngle = ninetyDegrees * 3;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch (IOException e) {
            return new BitmapDrawable(mContext.getResources(), bitmap);
        }
        if (rotationAngle != 0) {
            matrix.postRotate(rotationAngle);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(mContext.getResources(), bitmap);
    }

    /**
     * Inner Class
     */
    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<GalleryLoader> galleryLoaderReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, GalleryLoader galleryLoader) {
            super(res, bitmap);
            galleryLoaderReference = new WeakReference<>(galleryLoader);
        }

        public GalleryLoader getGalleryLoader() {
            return galleryLoaderReference.get();
        }

    }

}
