package ua.gov.dp.econtact.util;

import android.content.Context;

/**
 * Created by eva
 */
public final class UiUtil {

    private UiUtil() {
    }

    public static int getStatusBarHeight(final Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
