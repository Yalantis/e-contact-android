package ua.gov.dp.econtact.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import ua.gov.dp.econtact.App;

/**
 * Created by Yalantis
 * 2015/08/22.
 *
 * @author Artem Kholodnyi
 */
public final class DisplayUtil {

    private DisplayUtil() {

    }

    public static Point getDisplaySize() {
        WindowManager wm = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
