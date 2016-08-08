package ua.gov.dp.econtact.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.internal.MDTintHelper;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.model.category.Category;
import ua.gov.dp.econtact.model.category.CategoryWithImages;

import java.util.List;

/**
 * Created by Yalantis
 * 09.09.2015.
 *
 * @author Aleksandr
 */
public class CategoriesRadioAdapter extends BaseListAdapter<CategoriesRadioAdapter.AdapterItem> {

    private int mTintColor;

    public CategoriesRadioAdapter(final Context context, final List<CategoriesRadioAdapter.AdapterItem> list) {
        super(context, list, R.layout.item_category_radio);
        mTintColor = getResources().getColor(R.color.status_bar_color);
    }

    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = getLayout();
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AdapterItem item = getItem(position);
        holder.radioButton.setChecked(item.isChecked);
        MDTintHelper.setTint(holder.radioButton, mTintColor);
        holder.textView.setText(item.category.getName());

        return convertView;
    }

    public int getSelection() {
        for (int i = 0; i < getList().size(); i++) {
            if (getList().get(i).isChecked) {
                return i;
            }
        }
        return -1;
    }

    public void select(final int position) {
        for (int i = 0; i < getList().size(); i++) {
            getList().get(i).isChecked = i == position;
        }

        notifyDataSetChanged();
    }

    private class ViewHolder {

        RadioButton radioButton;
        TextView textView;

        ViewHolder(View view) {
            radioButton = (RadioButton) view.findViewById(R.id.control);
            textView = (TextView) view.findViewById(R.id.title);
        }
    }

    public static class AdapterItem {

        CategoryWithImages category;
        boolean isChecked;

        public AdapterItem(final CategoryWithImages category, final boolean isChecked) {
            this.category = category;
            this.isChecked = isChecked;
        }
    }
}
