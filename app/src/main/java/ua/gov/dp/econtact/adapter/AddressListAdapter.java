package ua.gov.dp.econtact.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.model.address.BaseAddress;
import ua.gov.dp.econtact.util.KeyboardUtils;
import ua.gov.dp.econtact.util.Translate;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yalantis
 * 26.11.2014.
 *
 * @author ED
 */
public class AddressListAdapter extends BaseListAdapter<BaseAddress> implements Filterable {
    private List<BaseAddress> mAddresses;
    private List<BaseAddress> mResultAddresses = new LinkedList<>();

    public AddressListAdapter(final Context context, final List<BaseAddress> addresses) {
        super(context, addresses, R.layout.item_address);
        mAddresses = addresses;
    }

    @Override
    public int getCount() {
        return mResultAddresses.size();
    }

    @Override
    public BaseAddress getItem(final int position) {
        return mResultAddresses.get(position);
    }

    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null) {
            convertView = getLayout();
        }
        ViewHolder viewHolder;
        if (convertView.getTag() != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.bindData(mResultAddresses.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(final Object resultValue) {
                return ((BaseAddress) (resultValue)).getTitle();
            }

            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<BaseAddress> addressList = findAddress(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = addressList;
                    filterResults.count = addressList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(final CharSequence constraint, final FilterResults results) {
                if (results != null && results.count > 0) {
                    mResultAddresses = (List<BaseAddress>) results.values;
                } else {
                    mResultAddresses.clear();
                }
                notifyDataSetChanged();
            }
        };

    }

    private List<BaseAddress> findAddress(String addressInput) {
        addressInput = Translate.translate(addressInput);
        List<BaseAddress> baseAddresses = new LinkedList<>();
        for (BaseAddress address : mAddresses) {
            if (address.getTitle().toLowerCase().contains(addressInput.toLowerCase())
                    || !TextUtils.isEmpty(address.getNameRu())
                    && address.getNameRu().toLowerCase().contains(addressInput.toLowerCase())) {
                baseAddresses.add(address);
            }
        }
        return baseAddresses;
    }

    public class ViewHolder {

        private TextView mTextViewTitle;

        public ViewHolder(final View itemView) {
            mTextViewTitle = (TextView) itemView.findViewById(R.id.txt_title);
        }

        void bindData(final BaseAddress address) {
            mTextViewTitle.setText(KeyboardUtils.getLocale(getContext()).equals(Const.RU)
                    && !TextUtils.isEmpty(address.getNameRu())
                    ? address.getNameRu() : address.getTitle());
        }
    }
}
