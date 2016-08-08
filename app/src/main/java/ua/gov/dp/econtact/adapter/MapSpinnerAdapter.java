package ua.gov.dp.econtact.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.model.StatusSpinnerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yalantis
 *
 * @author Penzykov
 */
public class MapSpinnerAdapter extends BaseAdapter {

    public static final String TAG_TITLE = "tag_non_dropdown";
    public static final String TAG_DROPDOWN = "tag_dropdown";

    protected List<StatusSpinnerItem> mItems = new ArrayList<>();

    public MapSpinnerAdapter(final List<StatusSpinnerItem> items) {
        mItems.addAll(items);
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(final StatusSpinnerItem item) {
        mItems.add(item);
    }

    public void addItems(final List<StatusSpinnerItem> items) {
        mItems.addAll(items);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getDropDownView(final int position, View view, final ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(TAG_DROPDOWN)) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner_dropdown, parent, false);
            view.setTag(TAG_DROPDOWN);
        }

        ((TextView) view.findViewById(R.id.text_view_item)).setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals(TAG_TITLE)) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner_title, parent, false);
            view.setTag(TAG_TITLE);
        }
        ((TextView) view.findViewById(R.id.text_view_title)).setText(getTitle(position));
        return view;
    }

    public String getTitle(final int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).getItemName() : "";
    }
}
