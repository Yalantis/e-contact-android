package ua.gov.dp.econtact.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by Yalantis
 * 2015/08/22.
 *
 * @author Artem Kholodnyi
 */
public final class BitmapFileUtil {

    private BitmapFileUtil() {

    }

    public static void saveBitmapToFile(final Bitmap bitmap, final File file) {
        FileOutputStream fOut;
        try {
            final int qualityPercent = 85;
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, qualityPercent, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Timber.e(e, "Error saving bitmap to file");
        }
    }
}
