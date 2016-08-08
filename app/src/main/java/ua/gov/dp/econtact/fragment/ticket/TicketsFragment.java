package ua.gov.dp.econtact.fragment.ticket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.adapter.TicketsFragmentAdapter;
import ua.gov.dp.econtact.fragment.BaseFragment;
import ua.gov.dp.econtact.fragment.BaseListFragment;
import ua.gov.dp.econtact.model.TicketStates;

/**
 * Created by eva on 25.07.15.§
 */
public class TicketsFragment extends BaseFragment {

    private static final String KEY_NEW_TICKET = "NEW_TICKET";

    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    private TicketsFragmentAdapter mAdapter;

    private boolean isFromNewTicket;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;


    public static TicketsFragment newInstance(final boolean isFromNewTicket, ViewPager.OnPageChangeListener onPageChangeListener) {
        TicketsFragment ticketsFragment = new TicketsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_NEW_TICKET, isFromNewTicket);
        ticketsFragment.setOnPageChangeListener(onPageChangeListener);
        ticketsFragment.setArguments(bundle);
        return ticketsFragment;
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromNewTicket = getArguments().getBoolean(KEY_NEW_TICKET);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        return mView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tickets;
    }

    private void setupViewPager(final ViewPager viewPager) {
        mAdapter = new TicketsFragmentAdapter(getChildFragmentManager());
        mAdapter.addFragment(TicketsListFragment.newInstance(TicketStates.IN_PROGRESS), getString(R.string.tab_in_progress));
        mAdapter.addFragment(TicketsListFragment.newInstance(TicketStates.DONE), getString(R.string.tab_done));
        mAdapter.addFragment(TicketsListFragment.newInstance(TicketStates.PENDING), getString(R.string.task_pending));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mAdapter);
        if (isFromNewTicket) {
            viewPager.setCurrentItem(2);
        }
    }

    public void updateList() {
        if (mAdapter != null) {
            for (BaseListFragment fragment : mAdapter.getFragments()) {
                if (fragment != null && fragment.isAdded()) {
                    fragment.updateList();
                }
            }
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener){
        mOnPageChangeListener = listener;
    }
}
