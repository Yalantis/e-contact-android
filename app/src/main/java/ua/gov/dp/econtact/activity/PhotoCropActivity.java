package ua.gov.dp.econtact.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.edmodo.cropper.CropImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.interfaces.PhotoSavedCallback;
import ua.gov.dp.econtact.util.SavePhotoTask;

public class PhotoCropActivity extends BaseActivity implements PhotoSavedCallback {

    public static final String KEY_PHOTO_PATH = "PHOTO_PATH";

    private static final String CROPPED = "cropped";
    private static final String FORMAT = ".jpg";
    private static final int ROTATION_DEGREES = 90;

    @Bind(R.id.progress)
    View mProgressBar;
    @Bind(R.id.photo)
    CropImageView mCropView;

    private Uri mUri;
    private Bitmap mBitmap;
    private int mAngle = 0;

    private boolean isCropping;
    private boolean isNeedToDisableMenu;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_crop);
        mUri = Uri.parse(getIntent().getStringExtra(NewTicketActivity.PHOTO));
        loadPhoto(mUri);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photo_crop, menu);
        if (isNeedToDisableMenu) {
            isNeedToDisableMenu = false;
            menu.findItem(R.id.apply).setEnabled(false);
            menu.findItem(R.id.rotate).setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.apply:
                if (mProgressBar.getVisibility() != View.VISIBLE) {
                    applyCrop();
                }
                return true;
            case R.id.rotate:
                if (mProgressBar.getVisibility() != View.VISIBLE) {
                    rotate();
                }
                return true;
            case R.id.cancel:
                cancel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Rotates photo
     */
    private void rotate() {
        final int fullCircle = 360;
        mAngle += ROTATION_DEGREES;
        mAngle %= fullCircle;
        mCropView.rotateImage(ROTATION_DEGREES);
    }

    private void showPhoto(final Bitmap bitmap) {
        mBitmap = bitmap;
        if (bitmap == null) {
            return;
        }
        mCropView.setImageBitmap(bitmap);
        mCropView.post(new Runnable() {
            @Override
            public void run() {
                mCropView.setGuidelines(1);
                mCropView.setAspectRatio(10, 10);
                mCropView.setFixedAspectRatio(true);
            }
        });
    }

    /**
     * Cropping bitmap
     */
    private void applyCrop() {
        if (isCropping || mBitmap == null) {
            return;
        }
        isCropping = true;
        final int width = mBitmap.getWidth();
        final int height = mBitmap.getHeight();
        String path = new File(getCacheDir(), CROPPED + System.currentTimeMillis() + FORMAT).getPath();
        mBitmap = mCropView.getCroppedImage();

        supportInvalidateOptionsMenu();
        SavePhotoTask task = new SavePhotoTask.Builder()
                .setAngle(mAngle)
                .setSize(width, height)
                .setRect(mCropView.getActualCropRect())
                .setContext(this)
                .setOutPath(path)
                .setCallback(this)
                .setUri(mUri)
                .build();
        task.execute();
    }

    protected void loadPhoto(final Uri uri) {
        // TODO: Useless
/*
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
*/
        Picasso.with(this).load(uri)
                .config(Bitmap.Config.RGB_565)
                .into(loadingTarget);
    }

    private Target loadingTarget = new Target() {

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
            // downscale mBitmap first (it can be very large)
            int width = App.getScreenWidth() < SavePhotoTask.MAX_IMAGE_SIZE
                    ? (int) SavePhotoTask.MAX_IMAGE_SIZE : App.getScreenWidth();
            mBitmap = bitmap;
            if (bitmap.getWidth() > width) {
                int height = (int) ((double) bitmap.getHeight() * (double) width / (double) bitmap.getWidth());
                mBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            }
            showPhoto(mBitmap);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onBitmapFailed(final Drawable errorDrawable) {
            mProgressBar.setVisibility(View.GONE);
            showPhoto(null);
        }

        @Override
        public void onPrepareLoad(final Drawable placeHolderDrawable) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void photoSaved(final String path) {
        setResult(RESULT_OK, new Intent().putExtra(KEY_PHOTO_PATH, path));
        finish();
    }

    private void cancel() {
        setResult(RESULT_OK, new Intent().putExtra(KEY_PHOTO_PATH, ""));
        finish();
    }
}
