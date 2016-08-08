package ua.gov.dp.econtact.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IntDef;

import ua.gov.dp.econtact.App;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public final class MediaUtil {

    private static final long MIN_RES = 1600;
    private static final long MAX_DURATION = 30100;

    private MediaUtil() {
    }

    public static boolean isLowResolution(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        return imageHeight < MIN_RES || imageWidth < MIN_RES;
    }

    public static boolean isBrokenImage(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        return imageHeight <= 0 || imageWidth <= 0;
    }

    public static boolean isShort(final String path) {
        MediaPlayer mp = MediaPlayer.create(App.getContext(), Uri.parse(path));
        if (mp == null) {
            return false;
        }
        int duration = mp.getDuration();
        mp.release();
        return duration < MAX_DURATION;
    }

    public static InputStream getInputStreamFromBitmap(final Bitmap bitmap,
                                                       final Bitmap.CompressFormat compressFormat,
                                                       final int quality) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, bos);
        byte[] bitmapData = bos.toByteArray();
        return new ByteArrayInputStream(bitmapData);
    }


    public static int getMaximumTextureSize() {
        //Get EGLConfigChooser
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        int[] version = new int[2];
        egl.eglInitialize(display, version);

        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);
        // Get maximum texture size from configs
        int[] textureSize = new int[1];
        int maximumTextureSize = 0;

        for (int i = 0; i < totalConfigurations[0]; i++) {
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

            if (maximumTextureSize < textureSize[0]) {
                maximumTextureSize = textureSize[0];
            }
        }

        egl.eglTerminate(display);
        return maximumTextureSize;
    }


    public static String getDataColumn(final Context context, final Uri uri,
                                       final String selection, final String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(final Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(final Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(final Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static Bitmap getVideoThumbnailFromPath(final String path) {
        ContentResolver resolver = App.getContext().getContentResolver();

        Cursor cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns._ID},
                MediaStore.MediaColumns.DATA + "=?",
                new String[]{path},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return MediaStore.Video.Thumbnails.getThumbnail(resolver, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }

    @IntDef({MediaStore.Video.Thumbnails.MINI_KIND, MediaStore.Video.Thumbnails.MICRO_KIND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoThumbKind {
    }


    public static Bitmap getNormalImages(final String path) {
        return turnPic(path, BitmapFactory.decodeFile(path));
    }

    public static Bitmap getNormalImages(final String path, final Bitmap bitmap) {
        return turnPic(path, bitmap);
    }


    private static Bitmap turnPic(final String path, Bitmap bitmap) {

        ExifInterface exif;
        final int ninetyDegrees = 90;
        int rotationAngle = 0;
        Matrix matrix = new Matrix();

        if (path != null) {
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
                e.printStackTrace();
                return bitmap;
            }
            if (rotationAngle != 0) {
                matrix.postRotate(rotationAngle);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    public static Bitmap getBitmap(final Long resourceId) {
        return MediaStore.Images.Thumbnails.getThumbnail(
                App.getContext().getContentResolver(), resourceId,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
    }
}
