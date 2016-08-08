package ua.gov.dp.econtact.gallery.workers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;

import java.io.File;

public class CacheManager {

    private final static String DISK_CACHE_SUB_DIR = "thumbnails";
    private final static int DISK_CACHE_SIZE = 1024 * 1024 * 90;

    //context
    private Context mContext;
    //LRUcache object for memory caching
    private LruCache<String, BitmapDrawable> mMemCache;
    //object for disk caching
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean isDiskCacheStarting = true;

    //Disk caching parameters

    public CacheManager(final Context context, final FragmentManager fragmentManager) {
        mContext = context;

        //creating disk cache
        File cacheDir = getDiskCacheDir(context, DISK_CACHE_SUB_DIR);
        new InitDiskCacheTask().execute(cacheDir);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/5th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 5;

        //getting cache memory state from saved state
        CacheRetainer retainFragment =
                CacheRetainer.findOrCreateRetainFragment(fragmentManager);
        mMemCache = retainFragment.mRetainedCache;

        //creating memory cache
        if (mMemCache == null) {
            mMemCache = new LruCache<String, BitmapDrawable>(cacheSize) {
                @Override
                protected int sizeOf(final String key, final BitmapDrawable bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return (int) (getSizeInBytes(bitmap.getBitmap()) / 1024);
                }

                @Override
                protected void entryRemoved(final boolean evicted, final String key,
                                            final BitmapDrawable oldValue, final BitmapDrawable newValue) {
                    super.entryRemoved(evicted, key, oldValue, newValue);
                }
            };
            retainFragment.mRetainedCache = mMemCache;
        }
    }

    /**
     * function  for adding bitmap to cache
     */
    public void addBitmapToCache(final String data, final BitmapDrawable value,
                                 final boolean addBitmapToDisk) {
        if ((data == null) || (value == null)) {
            return;
        }
        if (getBitmapFromMemCache(data) == null) {
            mMemCache.put(data, value);
        }

        if (!addBitmapToDisk) {
            return;
        }

        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && mDiskLruCache.get(data) == null) {
                mDiskLruCache.put(data, value.getBitmap());
            }
        }
    }

    /**
     * function for getting bitmap from memory cache
     *
     * @param key
     * @return bitmap drawable
     */
    public BitmapDrawable getBitmapFromMemCache(final String key) {
        return mMemCache.get(key);
    }

    /**
     * function for getting bitmap from disk cache
     *
     * @param data
     * @return BitmapDrawable
     */
    public BitmapDrawable getBitmapFromDiskCache(final String data) {

        Bitmap bitmap = null;
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (isDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    // TODO: handle exception
                }
            }
            if (mDiskLruCache != null) {
                bitmap = mDiskLruCache.get(data);
            }
        }

        if (bitmap != null) {
            return new BitmapDrawable(mContext.getResources(), bitmap);
        }
        return null;
    }


    /**
     * function for getting bitmap size in bites
     */
    @SuppressLint("NewApi")
    private long getSizeInBytes(final Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    /**
     * Creates a unique subdirectory of the designated app cache directory. Tries to use external
     * but if not mounted, falls back on internal storage.
     */
    public static File getDiskCacheDir(final Context context, final String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        String cachePath = context.getCacheDir().getPath();
        try {
            cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()
                    ? context.getExternalCacheDir().getPath()
                    : context.getCacheDir().getPath();
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
        return new File(cachePath + File.separator + uniqueName);
    }


    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(final File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                mDiskLruCache = DiskLruCache.openCache(cacheDir, DISK_CACHE_SIZE);
                isDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

}
