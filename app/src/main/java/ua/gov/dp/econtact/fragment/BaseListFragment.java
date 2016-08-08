package ua.gov.dp.econtact.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import timber.log.Timber;
import ua.gov.dp.econtact.R;

/**
 * @author eva on 25.07.15.
 */
public abstract class BaseListFragment extends BaseFragment {

    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected boolean isLoadingNow, isAllData;
    private LinearLayoutManager mLayoutManager;

    protected abstract void setupAdapter();

    public abstract void updateList();

    protected abstract void paging(final int itemCount);

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, mView);
        initViews();
        setupAdapter();
        return mView;
    }

    public void enableSwipeRefreshLayout(final boolean enable) {
        if (mSwipeRefreshLayout != null) {
            Timber.d("enableSwipeRefreshLayout: " + enable);
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }

    protected void initViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.list_tickets);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.status_bar_color));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                // enable/disable SwipeRefreshLayout
                if (mSwipeRefreshLayout != null && mLayoutManager.getChildCount() > 0) {
                    boolean isFirstVisible = mLayoutManager.findFirstVisibleItemPosition() == 0;
                    mSwipeRefreshLayout.setEnabled(isFirstVisible);
                }

                // load next page if necessary
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (!isLoadingNow && !isAllData) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        isLoadingNow = false;
                        paging(totalItemCount);
                    }
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                updateList();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSwipeRefreshLayout.removeAllViews();
    }
}
