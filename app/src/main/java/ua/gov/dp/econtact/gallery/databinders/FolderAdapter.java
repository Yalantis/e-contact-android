package ua.gov.dp.econtact.gallery.databinders;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.gallery.androidcustomgallery.GalleryActivity;
import ua.gov.dp.econtact.gallery.androidcustomgallery.GalleryImageSwitchFragment;
import ua.gov.dp.econtact.gallery.customcomponents.RecyclingImageView;
import ua.gov.dp.econtact.gallery.workers.CacheManager;

import java.util.LinkedHashMap;

public class FolderAdapter extends DataAdapter {

    private LinkedHashMap<Integer, FolderData> mGalleryFolders;
    private Integer[] mFoldersName;


    /**
     * constructor
     */
    public FolderAdapter(final Context context, final int resource,
                         final LinkedHashMap<Integer, FolderData> galleryFolders,
                         final int galleryType, final CacheManager cacheManager) {
        super(context, resource, galleryType, cacheManager);
        mFoldersName = galleryFolders.keySet().toArray(new Integer[]{});
        mGalleryFolders = galleryFolders;
        super.setItemCount(mFoldersName.length);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        GalleryHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_gallery_item, parent, false);
            holder = new GalleryHolder();
            holder.folderCount = (TextView) convertView.findViewById(R.id.tv_count);
            holder.folderName = (TextView) convertView.findViewById(R.id.tv_folder);
            holder.folderThumbnail = (RecyclingImageView) convertView.findViewById(R.id.iv_thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }

        //setting folder name
        FolderData fd = mGalleryFolders.get(mFoldersName[position]);
        holder.folderName.setText(fd.getDisplayName());
        holder.folderCount.setText(String.valueOf(fd.getCount()));
        //loading thumbnail bitmap image

        if (!loadBitmap(GalleryActivity.IMAGES,
                (long) fd.getPreviewImageId(), holder.folderThumbnail, fd.getThumbnailPath())) {
            loadBitmap(GalleryActivity.VIDEOS,
                    (long) fd.getPreviewImageId(), holder.folderThumbnail, fd.getThumbnailPath());
        }

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Fragment fragment = GalleryImageSwitchFragment.newInstance(String.valueOf(mFoldersName[position]), getGalleryType());
                ((GalleryActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.ll_gridHolder, fragment, "tag")
                        .addToBackStack("tag")
                        .commit();
            }
        };

        holder.folderThumbnail.setOnClickListener(clickListener);
        holder.folderName.setOnClickListener(clickListener);
        return convertView;
    }


}
