package ua.gov.dp.econtact.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
public class CategoriesAdapter extends BaseListAdapter<CategoriesAdapter.CategoryAdapterItem> {

    private int mTintColor;

    public CategoriesAdapter(final Context context, final List<CategoryAdapterItem> list) {
        super(context, list, R.layout.item_category);
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

        CategoryAdapterItem item = getItem(position);
        holder.checkBox.setChecked(item.isChecked());
        MDTintHelper.setTint(holder.checkBox, mTintColor);
        holder.textView.setText(item.getCategory().getName());

        return convertView;
    }

    public void check(final View view, final int position) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.control);
        boolean isChecked = !checkBox.isChecked();
        getList().get(position).setIsChecked(isChecked);

        if (position == 0) {
            for (int i = 0; i < getList().size(); i++) {
                getList().get(i).setIsChecked(isChecked);
            }
        } else {
            boolean isFirstChecked = getList().get(0).isChecked();
            boolean isAllChecked = true;
            for (int i = 1; i < getList().size(); i++) {
                if (!getList().get(i).isChecked()) {
                    isAllChecked = false;
                    break;
                }
            }
            if (isFirstChecked != isAllChecked) {
                getList().get(0).setIsChecked(isAllChecked);
            }
        }

        notifyDataSetChanged();
    }

    private class ViewHolder {

        CheckBox checkBox;
        TextView textView;

        ViewHolder(View view) {
            checkBox = (CheckBox) view.findViewById(R.id.control);
            textView = (TextView) view.findViewById(R.id.title);
        }
    }

    public static class CategoryAdapterItem {

        private boolean isChecked;
        private CategoryWithImages category;

        public CategoryAdapterItem(final boolean isChecked, final CategoryWithImages category) {
            this.isChecked = isChecked;
            this.category = category;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setIsChecked(final boolean isChecked) {
            this.isChecked = isChecked;
        }

        public CategoryWithImages getCategory() {
            return category;
        }

        public void setCategory(final CategoryWithImages category) {
            this.category = category;
        }
    }
}
