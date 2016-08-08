package ua.gov.dp.econtact.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.gov.dp.econtact.R;

/**
 * Created by Yalantis
 * 06.10.2014.
 *
 * @author Andrew Hristyan
 */
public class ImageSourcePickerFragment extends BaseDialogFragment
        implements DialogInterface.OnClickListener {

    private ImageSourceListener mListener;

    public static ImageSourcePickerFragment newInstance() {
        return new ImageSourcePickerFragment();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ImageSourceListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " should implement " + ImageSourceListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        List<AdapterItem> items = new ArrayList<>();
        items.add(new AdapterItem(R.drawable.ic_makephoto, R.string.image_take_photo));
        items.add(new AdapterItem(R.drawable.ic_fromgallery, R.string.image_from_gallery));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(new ImageAdapter(items), this);
        builder.setNegativeButton(R.string.lbl_cancel, null);

        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public String getFragmentTag() {
        return ImageSourcePickerFragment.class.getSimpleName();
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        switch (which) {
            case 0:
                mListener.onTake();
                break;
            case 1:
                mListener.onChooseExisting();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface ImageSourceListener {

        void onTake();

        void onChooseExisting();

    }

    private class AdapterItem {

        int iconResId;
        int stringResId;

        public AdapterItem(final int iconResId, final int stringResId) {
            this.iconResId = iconResId;
            this.stringResId = stringResId;
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private List<AdapterItem> mItems;

        public ImageAdapter(final List<AdapterItem> items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems != null ? mItems.size() : 0;
        }

        @Override
        public Object getItem(final int i) {
            return mItems != null ? mItems.get(i) : null;
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, final ViewGroup viewGroup) {
            AdapterItem item = mItems.get(i);
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_image, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.image_text);
            textView.setText(item.stringResId);
            textView.setCompoundDrawablesWithIntrinsicBounds(item.iconResId, 0, 0, 0);
            return textView;
        }
    }
}
