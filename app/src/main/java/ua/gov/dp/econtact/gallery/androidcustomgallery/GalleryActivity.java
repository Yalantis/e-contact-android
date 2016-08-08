package ua.gov.dp.econtact.gallery.androidcustomgallery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.BaseActivity;
import ua.gov.dp.econtact.gallery.databinders.DataHolder;
import ua.gov.dp.econtact.gallery.databinders.GalleryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * GalleryActivity - Custom Android Gallery<br>
 * Entry point of the app<br>
 * Displays all gallery folders<br>
 * It can be either video or image folder
 *
 * @author Ed Baev
 */
public class GalleryActivity extends BaseActivity implements GalleryAdapter.PhotoClickListener {

    //Result item
    public static final String RESULT = "result";
    public static final String VIDEOS = "videos";
    public static final String IMAGES = "images";

    // request code for starting child mActivity
    private static final int CHILD_REQUEST = 1;

    // UI
    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    // Data
    private int mMinPhoto;
    private int mMaxPhoto;
    private ArrayList<DataHolder> mFilePathsList;
    //Gallery Type
    private int mType;

    public static void navigateToGallery(final Activity activity, final int requestCode,
                                         final int type, final boolean isPortrait) {
        Intent galleryIntent = new Intent(activity, GalleryActivity.class);
        galleryIntent.putExtra(Const.IntentConstant.GALLERY_TYPE, type);
        galleryIntent.putExtra(Const.IntentConstant.PORTRAIT, isPortrait);
        activity.startActivityForResult(galleryIntent, requestCode);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mMinPhoto = getIntent().getIntExtra(Const.IntentConstant.MIN_QUANTITY, 0);
        mMaxPhoto = getIntent().getIntExtra(Const.IntentConstant.MAX_QUANTITY, 10);
        mType = getIntent().getIntExtra(Const.IntentConstant.GALLERY_TYPE, 0);
        mFilePathsList = new ArrayList<>();
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        setUpToolbar();
        if (mMinPhoto > 0) {
            mFab.hide();
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(Const.IntentConstant.LIST_OF_PATHS, mFilePathsList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_gridHolder, GallerySwitcherFragment.newInstance(mType)).commit();

    }
    private void setTitle() {
        mToolbar.setTitle(getResources().getQuantityString(R.plurals.gallery_title, mFilePathsList.size(),
                mFilePathsList.size(), mMaxPhoto));
    }


    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onBackPressed();
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == CHILD_REQUEST) {
            switch (resultCode) {
                case RESULT_CANCELED:
                    finish();
                    break;
                case RESULT_OK:
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(GalleryActivity.RESULT,
                            data.getParcelableArrayListExtra(RESULT));
                    this.setResult(RESULT_OK, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onPhotoClick() {
        if (mFilePathsList.size() > mMinPhoto - 1) {
            mFab.show();
        } else {
            mFab.hide();
        }
        setTitle();
    }

    public List<DataHolder> getFilePathsList() {
        return mFilePathsList;
    }

    public void setFilePathsList(final ArrayList<DataHolder> filePathsList) {
        mFilePathsList = filePathsList;
    }

    public int getMaxPhoto() {
        return mMaxPhoto;
    }

    public int getType() {
        return mType;
    }

}
