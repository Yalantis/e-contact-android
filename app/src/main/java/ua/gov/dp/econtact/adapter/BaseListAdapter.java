package ua.gov.dp.econtact.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yalantis
 *
 * @author Dmitriy Dovbnya
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    private final Object mLock = new Object();

    private List<T> mList;
    private Context mContext;
    private int mLayoutId;
    private LayoutInflater mInflater;

    public BaseListAdapter(final Context context, final List<T> list, final int layoutId) {
        mContext = context;
        mLayoutId = layoutId;
        mList = list;
    }

    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        return getLayout();
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(final T object) {
        synchronized (mLock) {
            mList.add(object);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(final Collection<? extends T> collection) {
        synchronized (mLock) {
            mList.addAll(collection);
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    @SafeVarargs
    public final void addAll(final T... items) {
        synchronized (mLock) {
            Collections.addAll(mList, items);
        }
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(final T object, final int index) {
        synchronized (mLock) {
            mList.add(index, object);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(final T object) {
        synchronized (mLock) {
            mList.remove(object);
        }
        notifyDataSetChanged();
    }

    /**
     * Remove all elements from the mList.
     */
    public void clear() {
        synchronized (mLock) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(final Comparator<? super T> comparator) {
        synchronized (mLock) {
            Collections.sort(mList, comparator);
        }
        notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getItem(final int position) {
        return (mList != null && position >= 0 && position < getCount()) ? mList.get(position) : null;
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(final T item) {
        return mList != null ? mList.indexOf(item) : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(final int position) {
        return position;
    }

    /**
     * Returns the mContext associated with this array adapter. The mContext is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }

    public Resources getResources() {
        return getContext().getResources();
    }

    public void setList(final List<T> list) {
        synchronized (mLock) {
            mList = list;
        }
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return mList;
    }

    public View getLayout() {
        return getInflater().inflate(mLayoutId, null);
    }

    public LayoutInflater getInflater() {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(mContext);
        }
        return mInflater;
    }

}
