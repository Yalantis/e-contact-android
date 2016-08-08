package ua.gov.dp.econtact.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yalantis
 * 09.09.2015.
 *
 * @author Aleksandr
 */
public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private SparseBooleanArray mSelectedItems;

    public SelectableAdapter() {
        mSelectedItems = new SparseBooleanArray();
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */

    public boolean isSelected(final int position) {
        return getSelectedItems().contains(position);
    }


    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */

    public void toggleSelection(final int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }


    /**
     * Clear the selection status for all items
     */

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        mSelectedItems.clear();

        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }


    /**
     * Count the selected items
     *
     * @return Selected items count
     */

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }


    /**
     * Indicates the list of selected items
     *
     * @return List of selected items ids
     */

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); ++i) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

}
