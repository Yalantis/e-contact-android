package ua.gov.dp.econtact.gallery.workers;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;

/**
 * CacheRetainer - cache state retainer
 *
 * @author Ed Baev
 */
public class CacheRetainer extends Fragment {

    private static final String FRAGMENT_TAG = "RetainFragment";

    public LruCache<String, BitmapDrawable> mRetainedCache;

    public CacheRetainer() {
    }

    public static CacheRetainer findOrCreateRetainFragment(final FragmentManager fm) {
        CacheRetainer fragment = (CacheRetainer) fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new CacheRetainer();
            fm.beginTransaction().add(fragment, FRAGMENT_TAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
