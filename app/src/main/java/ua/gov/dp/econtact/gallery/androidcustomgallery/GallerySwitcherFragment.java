package ua.gov.dp.econtact.gallery.androidcustomgallery;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.gallery.databinders.FolderAdapter;
import ua.gov.dp.econtact.gallery.databinders.FolderData;
import ua.gov.dp.econtact.gallery.workers.CacheManager;

import java.util.LinkedHashMap;

/**
 * GallerySwitcherFragment - fragment holding separated video
 * and images in parent mActivity
 * Used with Actionbar Tabs
 *
 * @author Ed Baev
 */
public class GallerySwitcherFragment extends Fragment {

    //constants, the keys for mActivity operations
    public static final String ARGS = "arguments";
    private static final int READ_STORAGE_PERMISSIONS_REQUEST = 1;

    private GridView mGalleryView;

    //current gallery type - video or image
    private int mGalleryType;

    //cache for thumbnail images
    private CacheManager mCacheManager = null;

    public static GallerySwitcherFragment newInstance(final int mGalleryType) {
        GallerySwitcherFragment instance = new GallerySwitcherFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS, mGalleryType);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery_view, container, false);
        Bundle args = getArguments();
        mGalleryType = args.getInt(ARGS);
        mCacheManager = new CacheManager(getActivity(),
                getActivity().getSupportFragmentManager());
        mGalleryView = (GridView) rootView.findViewById(R.id.gv_gallery);
        getPermissionToReadStorage();
        return rootView;
    }


    /**
     * Populating items from gallery to list
     */
    private void setGalleryAdapter() {
        LinkedHashMap<Integer, FolderData> folders = readGallery((GalleryActivity) getActivity());
        //forcing garbage collection
        System.gc();
        mGalleryView.setAdapter(new FolderAdapter(getActivity()
                , R.layout.gridview_gallery_item, folders, mGalleryType, mCacheManager));
    }

    /**
     * function for reading gallery folders
     *
     * @param activity current mActivity context
     * @return Map containing bucket id as key and FolderData object
     */
    public static LinkedHashMap<Integer, FolderData> readGallery(final GalleryActivity activity) {

        Cursor cursor;
        int columnBucketId, columnThumbnailData, columnIndexFolderName, columnPath;
        //list of all gallery folders
        LinkedHashMap<Integer, FolderData> listOfAllImages = new LinkedHashMap<>();

        /*** Images ***/
        String[] projection;
        Uri galleryUri;
        if (activity.getType() == Const.GalleryType.IMAGE || activity.getType() == Const.GalleryType.IMAGE_VIDEO) {
            galleryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.DATE_TAKEN

            };
            cursor = activity.getContentResolver().query(galleryUri, projection, null, null,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC, "
                            + MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            columnBucketId = cursor.getColumnIndexOrThrow(projection[0]);
            columnIndexFolderName = cursor.getColumnIndexOrThrow(projection[1]);
            columnThumbnailData = cursor.getColumnIndexOrThrow(projection[2]);
            columnPath = cursor.getColumnIndexOrThrow(projection[3]);

            //getting data from cursor and saving it in treemap
            FolderData folderData;
            while (cursor.moveToNext()) {
                int bucketId = cursor.getInt(columnBucketId);

                folderData = listOfAllImages.get(bucketId);

                if (null == folderData) {
                    // First preview from this bucket
                    folderData = new FolderData();

                    folderData.setDisplayName(cursor.getString(columnIndexFolderName));
                    folderData.setPreviewImageId(cursor.getInt(columnThumbnailData));
                    folderData.increaseCount();
                    folderData.setPreviewPath(cursor.getString(columnPath));

                    listOfAllImages.put(cursor.getInt(columnBucketId), folderData);
                } else {
                    // We already have the preview, just increase the count
                    folderData.increaseCount();
                }
            }
            cursor.close();
        }
        /*** Videos ***/
        if (activity.getType() == Const.GalleryType.VIDEO || activity.getType() == Const.GalleryType.IMAGE_VIDEO) {
            galleryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.Thumbnails._ID,
                    MediaStore.Video.VideoColumns.DATA
            };
            FolderData folderData;
            cursor = activity.getContentResolver().query(galleryUri, projection, null, null,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC");

            if (cursor == null) {
                return listOfAllImages;
            }

            columnBucketId = cursor.getColumnIndexOrThrow(projection[0]);
            columnIndexFolderName = cursor.getColumnIndexOrThrow(projection[1]);
            columnThumbnailData = cursor.getColumnIndexOrThrow(projection[2]);
            columnPath = cursor.getColumnIndexOrThrow(projection[3]);

            while (cursor.moveToNext()) {
                int bucketId = cursor.getInt(columnBucketId);

                folderData = listOfAllImages.get(bucketId);

                if (null == folderData) {
                    // First preview from this bucket
                    folderData = new FolderData();

                    folderData.setDisplayName(cursor.getString(columnIndexFolderName));
                    folderData.setPreviewImageId(cursor.getInt(columnThumbnailData));
                    folderData.increaseCount();
                    folderData.setPreviewPath(cursor.getString(columnPath));

                    listOfAllImages.put(cursor.getInt(columnBucketId), folderData);
                } else {
                    // We already have the preview, just increase the count
                    folderData.increaseCount();
                }
            }
            cursor.close();
        }
        return listOfAllImages;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToReadStorage() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // TODO: make some action here
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSIONS_REQUEST);
        } else {
            setGalleryAdapter();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == READ_STORAGE_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setGalleryAdapter();
            } else {
                getActivity().finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
