package ua.gov.dp.econtact.gallery.databinders;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.gallery.androidcustomgallery.GalleryActivity;
import ua.gov.dp.econtact.gallery.customcomponents.GalleryFrameLayout;
import ua.gov.dp.econtact.gallery.customcomponents.RecyclingImageView;
import ua.gov.dp.econtact.gallery.workers.CacheManager;
import ua.gov.dp.econtact.util.MediaUtil;
import ua.gov.dp.econtact.util.Toaster;

import java.util.ArrayList;
import java.util.List;

/**
 * GalleryAdapter - Custom adapter for listing all
 * items in a gallery folder
 *
 * @author Ed Baev
 */
public class GalleryAdapter extends DataAdapter {

    private static final String PREFERENCES_FILE = "dialog_settings";

    private ArrayList<DataHolder> mFiles;
    private PhotoClickListener mPhotoClickListener;
    private int mMaxSelect;
    private List<DataHolder> mDataList;

    /**
     * constructor
     */
    public GalleryAdapter(final Context context, final int resource, final int galleryType,
                          final ArrayList<DataHolder> data, final CacheManager cacheManager,
                          final List<DataHolder> list, final int maxSelect) {
        super(context, resource, galleryType, cacheManager);
        mFiles = data;
        mDataList = list;
        mMaxSelect = maxSelect;
        setItemCount(mFiles.size());
    }

    public void setPhotoClickListener(final PhotoClickListener photoClickListener) {
        mPhotoClickListener = photoClickListener;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (mFiles.get(position) == null) {
            return null;
        }
        final int dataType = mFiles.get(position).getDataType();
        final String mediaPath = mFiles.get(position).getMediaPath();
        final GalleryHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_child_gallery_item, parent, false);
            holder = new GalleryHolder();
            holder.folderThumbnail = (RecyclingImageView) convertView.findViewById(R.id.iv_thumbnail);
            holder.baseLayout = (GalleryFrameLayout) convertView.findViewById(R.id.baseItemLayout);
            holder.videoIcon = (ImageView) convertView.findViewById(R.id.ivVideoIcon);
            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }

        if (mDataList.contains(mFiles.get(position))) {
            if (dataType == Const.Data.IMAGE && !MediaUtil.isBrokenImage(mediaPath)) {
                holder.baseLayout.setChecked(MediaUtil.isLowResolution(mediaPath)
                        ? GalleryFrameLayout.LOW : GalleryFrameLayout.CHECK);
            } else {
                holder.baseLayout.setChecked(GalleryFrameLayout.CHECK);
            }
        } else {
            holder.baseLayout.setChecked(GalleryFrameLayout.UNCHECK);
        }

        //loading thumbnail bitmap image
        loadBitmap(dataType == Const.Data.IMAGE ? GalleryActivity.IMAGES : GalleryActivity.VIDEOS,
                Long.valueOf(mFiles.get(position).getThumbnailData()),
                holder.folderThumbnail, mFiles.get(position).getMediaPath());

        holder.videoIcon.setVisibility(dataType == Const.Data.VIDEO ? View.VISIBLE : View.GONE);

        //click listener for each view item
        holder.baseLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                //update the view state
                updateViewState(holder.baseLayout, position);
            }
        });
        return convertView;
    }

    /**
     * function for clearing selected item mDataList
     */
    public void clearSelectedItemList() {
        mDataList.clear();
    }

    /**
     * function for getting all selected item mDataList
     *
     * @return mDataList of selected indices
     */
    // TODO: Wrong List data type on exit
    public ArrayList<String> getSelectedItemList() {
        return (ArrayList) mDataList;
    }

    /**
     * function for setting mDataList of indices
     *
     * @param items mDataList of indices
     */
    public void setSelectedItemList(final ArrayList<DataHolder> items) {
        mDataList = items;
    }

    /**
     * function for updating view state
     */
    private void updateViewState(final GalleryFrameLayout frameLayout, final int itemPosition) {
        DataHolder file = mFiles.get(itemPosition);
        final int dataType = file.getDataType();
        final String mediaPath = file.getMediaPath();

        if (frameLayout.getChecked() == GalleryFrameLayout.CHECK
                || frameLayout.getChecked() == GalleryFrameLayout.LOW) {
            mDataList.remove(file);
            frameLayout.setChecked(GalleryFrameLayout.UNCHECK);
        } else {
            if (mDataList.size() < mMaxSelect) {
                if (dataType == Const.Data.IMAGE) {
                    if (!MediaUtil.isBrokenImage(mediaPath)) {
                        if (MediaUtil.isLowResolution(mediaPath)) {
                            mDataList.add(file);
                            frameLayout.setChecked(GalleryFrameLayout.LOW);
                        } else {
                            putCheckInList(file, frameLayout);
                        }
                    } else {
                        Toaster.showShort(getContext().getString(R.string.error_broken_photo));
                    }
                } else {
                    if (MediaUtil.isShort(mediaPath)) {
                        putCheckInList(file, frameLayout);
                    } else {
                        Toaster.showShort(R.string.video_toolong);
                    }
                }
            } else {
                Toaster.showShort("You can’t select more photos for this product");
            }
        }
        if (mPhotoClickListener != null) {
            mPhotoClickListener.onPhotoClick();
        }
    }


    private void putCheckInList(final DataHolder path, final GalleryFrameLayout frameLayout) {
        mDataList.add(path);
        frameLayout.setChecked(GalleryFrameLayout.CHECK);
    }

    public static void saveSharedSetting(final Context ctx, final String settingName, final String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(final Context ctx, final String settingName, final String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    /**
     * Interface
     */
    public interface PhotoClickListener {
        void onPhotoClick();
    }
}
