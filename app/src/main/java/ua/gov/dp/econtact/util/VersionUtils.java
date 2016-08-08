package ua.gov.dp.econtact.util;

import android.os.Build;

/**
 * Created by Yalantis
 *
 * @author Oleksii Shliama.
 */
public final class VersionUtils {

    private VersionUtils() {
    }

    public static boolean isAtLeastL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAtJellyBeen() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }
}
