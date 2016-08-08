package ua.gov.dp.econtact.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;

import timber.log.Timber;
import ua.gov.dp.econtact.interfaces.PhotoSavedCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yalantis
 */
public class SavePhotoTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = SavePhotoTask.class.getSimpleName();
    public static final double MAX_IMAGE_SIZE = 1200;

    private Context mContext;
    private Uri mUri;
    private String mOutPath;
    private int mWidth;
    private int mHeight;
    private int mAngle;
    private RectF mRect;
    private PhotoSavedCallback mCallback;

    public SavePhotoTask(final Context context, final Uri uri, final String outPath,
                         final int angle, final int width, final int height,
                         final RectF rect, final PhotoSavedCallback callback) {
        mContext = context;
        mUri = uri;
        mOutPath = outPath;
        mRect = rect;
        mWidth = width;
        mHeight = height;
        mAngle = angle;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(final Void... params) {
        Bitmap src = null;
        try {
            src = Picasso.with(mContext).load(mUri).get();
        } catch (IOException e) {
            Log.e(TAG, "Image not found: " + mUri);
        }
        if (src == null) {
            Log.e(TAG, "Image not found: " + mUri);
            return null;
        }

        float koefW = (float) mWidth / (float) src.getWidth();
        float koefH = (float) mHeight / (float) src.getHeight();

        mRect.top /= koefH;
        mRect.left /= koefW;
        mRect.right /= koefW;
        mRect.bottom /= koefH;
        mWidth = (int) mRect.width();
        mHeight = (int) mRect.height();

        Matrix matrix = new Matrix();
        matrix.preRotate(mAngle);

        Bitmap bitmap = getScaledBitmap(src, matrix);
        FileOutputStream fos = null;
        final int qualityPercent = 90;
        try {
            fos = new FileOutputStream(new File(mOutPath));

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, qualityPercent, fos);
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage(), e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Timber.e(e,"Error writing a file");
            }
        }

        return null;
    }

    private Bitmap getScaledBitmap(Bitmap src, Matrix matrix) {
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        bitmap = Bitmap.createBitmap(bitmap, (int) mRect.left, (int) mRect.top, mWidth, mHeight);

        if (Math.max(mWidth, mHeight) > MAX_IMAGE_SIZE) {
            if (mWidth > mHeight) {
                mHeight = (int) ((double) mHeight * MAX_IMAGE_SIZE / (double) mWidth);
                mWidth = (int) MAX_IMAGE_SIZE;
            } else {
                mWidth = (int) ((double) mWidth * MAX_IMAGE_SIZE / (double) mHeight);
                mHeight = (int) MAX_IMAGE_SIZE;
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, false);
            bitmap.recycle();
            bitmap = scaledBitmap;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        super.onPostExecute(aVoid);
        if (mCallback != null) {
            mCallback.photoSaved(mOutPath);
        }
    }

    public static class Builder {
        private PhotoSavedCallback callback;
        private Context context;
        private Uri uri;
        private String outPath;
        private int angle = -1;
        private int width;
        private int height;
        private RectF rect;

        public Builder setContext(final Context context) {
            this.context = context;
            return this;
        }

        public Builder setUri(final Uri uri) {
            this.uri = uri;
            return this;
        }

        public Builder setOutPath(final String outPath) {
            this.outPath = outPath;
            return this;
        }

        public Builder setAngle(final int angle) {
            this.angle = angle;
            return this;
        }

        public Builder setSize(final int width, final int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setRect(final RectF rect) {
            this.rect = rect;
            return this;
        }

        public Builder setCallback(final PhotoSavedCallback callback) {
            this.callback = callback;
            return this;
        }

        public SavePhotoTask build() {
            return new SavePhotoTask(context, uri, outPath, angle, width, height, rect, callback);
        }
    }

}

