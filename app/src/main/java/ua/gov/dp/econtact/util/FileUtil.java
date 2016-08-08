package ua.gov.dp.econtact.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by Yalantis
 * 22.12.2014.
 *
 * @author Andrew Hristyan
 */
public final class FileUtil {

    private FileUtil() {

    }

    public static File createImageFile(final Context context) throws IOException {
        return File.createTempFile("IMG_", ".jpg", getCacheDir(context));
    }

    public static File getCacheDir(final Context context) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = context.getExternalCacheDir();
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Timber.e("Failed to create directory");
                    }
                }
            }
        } else {
            Timber.e("External storage is not mounted READ/WRITE.");
        }
        if (storageDir == null) {
            return context.getCacheDir();
        }
        return storageDir;
    }
}
