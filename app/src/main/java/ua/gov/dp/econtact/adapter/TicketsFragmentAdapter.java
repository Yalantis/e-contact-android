package ua.gov.dp.econtact.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ua.gov.dp.econtact.fragment.BaseListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexey on 10.03.2016.
 */
public class TicketsFragmentAdapter extends FragmentPagerAdapter {

    private final List<BaseListFragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public TicketsFragmentAdapter(final FragmentManager fm) {
        super(fm);
    }


    public List<BaseListFragment> getFragments() {
        return mFragments;
    }

    public void addFragment(final BaseListFragment fragment, final String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public BaseListFragment getItem(final int position) {
        return mFragments.get(position);
    }


    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        return mFragmentTitles.get(position);
    }
}
