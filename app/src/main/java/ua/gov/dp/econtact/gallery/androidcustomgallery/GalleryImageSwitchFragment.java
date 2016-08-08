package ua.gov.dp.econtact.gallery.androidcustomgallery;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.gallery.DataHolderComparator;
import ua.gov.dp.econtact.gallery.databinders.DataHolder;
import ua.gov.dp.econtact.gallery.databinders.GalleryAdapter;
import ua.gov.dp.econtact.gallery.workers.CacheManager;

/**
 * Created by Yalantis
 * 7/16/2015.
 *
 * @author Pavel
 */
public class GalleryImageSwitchFragment extends Fragment {

    public static final String FILTER = "args";
    public static final String TYPE = "arguments";
    public static final String VIDEOS = "videos";
    public static final String IMAGES = "images";
    public static final String STATE_SELECTED_ITEM_COUNT = "itemcount";
    public static final String STATE_SAVED_ITEMS = "selecteditems";
    public static final int PREVIEW_REQUEST = 0;

    //fields needed from gallery content provider
    private static String[] sProjection;

    //gallery content provider uri
    private static Uri sGalleryUri;

    /*selection for content provider query
    here it will be folder name*/
    private static String sSelection;

    /*extra arguments from parent mActivity
    it would be gallery type*/
    private static String sFilterData;
    //constants, the keys for mActivity operations

    private GalleryActivity myContext;

    private GridView mGalleryView;

    //cache for thumbnail images
    private CacheManager mCacheManager = null;

    //custom adapter class for populating grid view
    private GalleryAdapter mGalleryAdapter;

    //current gallery type - video or image
    private int mGalleryType;

    public static GalleryImageSwitchFragment newInstance(final String filterData, final int galleryType) {
        GalleryImageSwitchFragment instance = new GalleryImageSwitchFragment();
        Bundle args = new Bundle();
        args.putString(FILTER, filterData);
        args.putInt(TYPE, galleryType);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(final Activity activity) {
        myContext = (GalleryActivity) activity;
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery_images_view, container, false);
        Bundle args = getArguments();
        sFilterData = args.getString(FILTER);
        mGalleryType = args.getInt(TYPE);
        if (savedInstanceState != null) {
            //retaining selected items
            ArrayList<DataHolder> list = savedInstanceState.getParcelableArrayList(STATE_SAVED_ITEMS);
            /*getting saved selected items from parcelable arraylist
            we are saving positions of selected grid view items*/
            myContext.setFilePathsList(list);
            mGalleryAdapter.setSelectedItemList(list);
            mGalleryAdapter.notifyDataSetChanged();
        }
        mCacheManager = new CacheManager(getActivity(), myContext.getSupportFragmentManager());
        mGalleryView = (GridView) rootView.findViewById(R.id.gv_gallery);
        //populating gallery
        this.populateChildGallery();
        return rootView;
    }

    /**
     * function for reading the gallery files and folders
     * and setting custom adapter for gallery grid view
     */
    private void populateChildGallery() {
        //listing all gallery files
        ArrayList<DataHolder> files = readGallery((GalleryActivity) getActivity());
        //forcing garbage collection
        System.gc();
        mGalleryAdapter = new GalleryAdapter(getActivity(),
                R.layout.gridview_child_gallery_item, mGalleryType, files, mCacheManager,
                myContext.getFilePathsList(), myContext.getMaxPhoto());
        mGalleryAdapter.setPhotoClickListener((GalleryActivity) getActivity());
        mGalleryView.setAdapter(mGalleryAdapter);
    }

    /**
     * function for reading gallery
     *
     * @param activity context of current mActivity
     * @return returns list of all gallery items
     */
    public ArrayList<DataHolder> readGallery(final GalleryActivity activity) {

        ArrayList<DataHolder> listOfAllImages = new ArrayList<>();
        prepareDataForReading();
        if (activity.getType() == Const.GalleryType.IMAGE
                || activity.getType() == Const.GalleryType.IMAGE_VIDEO) {
            listOfAllImages.addAll(getImages());
        }
        if (activity.getType() == Const.GalleryType.VIDEO
                || activity.getType() == Const.GalleryType.IMAGE_VIDEO) {
            listOfAllImages.addAll(getVideos());
        }
        Collections.sort(listOfAllImages, new DataHolderComparator());
        return listOfAllImages;
    }

    private void prepareDataForReading() {
        sGalleryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        sProjection = new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Media.DATE_TAKEN};
        sSelection = MediaStore.Images.Media.BUCKET_ID + " = ?";
    }

    public ArrayList<DataHolder> getImages() {
        Cursor galleryItemsCursor;
        int columnThumbnailData, columnFilePath, columnDataTaken;

        ArrayList<DataHolder> listOfAllImages = new ArrayList<DataHolder>();

        galleryItemsCursor = getActivity().getContentResolver().query(sGalleryUri, sProjection, sSelection,
                new String[]{sFilterData},
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (galleryItemsCursor == null) {
            return listOfAllImages;
        }

        columnDataTaken = galleryItemsCursor.getColumnIndexOrThrow(sProjection[2]);
        columnThumbnailData = galleryItemsCursor.getColumnIndexOrThrow(sProjection[1]);
        columnFilePath = galleryItemsCursor.getColumnIndexOrThrow(sProjection[0]);

        //getting data from cursor and saving it in Data holder
        while (galleryItemsCursor.moveToNext()) {
            DataHolder holder = new DataHolder();
            holder.setMediaPath(galleryItemsCursor.getString(columnFilePath));
            holder.setThumbnailData(galleryItemsCursor.getString(columnThumbnailData));
            holder.setDataTaken(galleryItemsCursor.getString(columnDataTaken));
            holder.setDataType(Const.Data.IMAGE);
            listOfAllImages.add(holder);
        }

        sGalleryUri = null;
        sProjection = null;
        galleryItemsCursor.close();
        return listOfAllImages;
    }

    public ArrayList<DataHolder> getVideos() {
        Cursor galleryItemsCursor;
        int columnThumbnailData, columnFilePath, columnDataTaken;
        prepareDataForReading();
        ArrayList<DataHolder> listOfAllImages = new ArrayList<>();
        galleryItemsCursor = getActivity().getContentResolver().query(sGalleryUri, sProjection, sSelection,
                new String[]{sFilterData},
                MediaStore.Video.Media.DATE_ADDED + " DESC");

        if (galleryItemsCursor == null) {
            return listOfAllImages;
        }

        columnDataTaken = galleryItemsCursor.getColumnIndexOrThrow(sProjection[2]);
        columnThumbnailData = galleryItemsCursor.getColumnIndexOrThrow(sProjection[1]);
        columnFilePath = galleryItemsCursor.getColumnIndexOrThrow(sProjection[0]);

        //getting data from cursor and saving it in Data holder
        while (galleryItemsCursor.moveToNext()) {
            DataHolder holder = new DataHolder();
            holder.setMediaPath(galleryItemsCursor.getString(columnFilePath));
            holder.setThumbnailData(galleryItemsCursor.getString(columnThumbnailData));
            holder.setDataTaken(galleryItemsCursor.getString(columnDataTaken));
            holder.setDataType(Const.Data.VIDEO);
            listOfAllImages.add(holder);
        }

        sGalleryUri = null;
        sProjection = null;
        galleryItemsCursor.close();
        return listOfAllImages;
    }


    @Override
    public void onSaveInstanceState(final Bundle outState) {
        ArrayList<DataHolder> list = (ArrayList) myContext.getFilePathsList();
        if (list.size() <= 0) {
            return;
        }

        outState.putParcelableArrayList(STATE_SAVED_ITEMS, list);
        super.onSaveInstanceState(outState);
    }


}
