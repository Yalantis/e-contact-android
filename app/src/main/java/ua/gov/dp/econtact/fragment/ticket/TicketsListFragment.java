package ua.gov.dp.econtact.fragment.ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.MainActivity;
import ua.gov.dp.econtact.activity.NewTicketActivity;
import ua.gov.dp.econtact.activity.TicketActivity;
import ua.gov.dp.econtact.adapter.TicketsAdapter;
import ua.gov.dp.econtact.api.task.ticket.LikeTask;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.event.tickets.TicketsByStateEvent;
import ua.gov.dp.econtact.fragment.BaseListFragment;
import ua.gov.dp.econtact.interfaces.ListListener;
import ua.gov.dp.econtact.interfaces.TicketListFragmentListener;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.util.Toaster;

/**
 * Created by Eva
 * 25.07.15.
 */
public class TicketsListFragment extends BaseListFragment implements ListListener {

    private static final String STATUS = "STATUS";

    private TicketStates mState;
    private List<Ticket> mTicketsList;

    private TicketsAdapter mTicketsAdapter;
    private ActionMode mActionMode;

    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    private TicketListFragmentListener mListener;

    @Bind(R.id.empty)
    View mEmptyView;

    public static TicketsListFragment newInstance(final TicketStates status) {
        Bundle args = new Bundle();
        args.putSerializable(STATUS, status);
        TicketsListFragment fragment = new TicketsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mListener = (TicketListFragmentListener) activity;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mState = (TicketStates) getArguments().getSerializable(STATUS);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mState == TicketStates.DRAFT) {
            enableSwipeRefreshLayout(false);
        } else {
            enableSwipeRefreshLayout(true);
            mSwipeRefreshLayout.setRefreshing(true);
        }
        getTicket(Const.DEFAULT_OFFSET);

    }

    @Override
    protected void setupAdapter() {
        if (mRecyclerView != null) {
            Set<Long> categoryIds = App.spManager.getCategoriesId();
            switch (mState) {
                case DRAFT:
                    mTicketsList = getDrafts();
                    break;
                case MY_TICKET:
                    mTicketsList = App.dataManager.getTicketsByUserId(App.spManager.getUserId());
                    break;
                default:
                    if (categoryIds.isEmpty()) {
                        mTicketsList = App.dataManager.getTicketsByState(mState.getStates());
                    } else {
                        mTicketsList = App.dataManager.getTicketsByStateFilter(mState.getStates(),
                                categoryIds.toArray(new Long[categoryIds.size()]));
                    }
                    break;
            }
        }

        mTicketsAdapter = new TicketsAdapter(getActivity(), mTicketsList, this);
        mRecyclerView.setAdapter(mTicketsAdapter);
        mEmptyView.setVisibility(mTicketsList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tickets_list;
    }

    @Override
    public void updateList() {
        getTicket(Const.DEFAULT_OFFSET);
    }

    @Override
    protected void paging(final int itemCount) {
        if (!isLoadingNow && mState != TicketStates.MY_TICKET) {
            getTicket(itemCount);
        }
    }

    @Override
    public void enableSwipeRefreshLayout(final boolean enable) {
        super.enableSwipeRefreshLayout(mState == null ? enable : !isDraftMode() && enable);
    }

    private void notifyDataSetChanged() {
        if (!mRecyclerView.isComputingLayout()) {
            mTicketsAdapter.notifyDataSetChanged();
            mEmptyView.setVisibility(mTicketsList.isEmpty() ? View.VISIBLE : View.GONE);
        }
        super.mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getTicket(final int offset) {
        if (isDraftMode()) {
            mTicketsList.clear();
            mTicketsList.addAll(getDrafts());
            notifyDataSetChanged();
        } else {
            Set<Long> categoryIds = App.spManager.getCategoriesId();
            mListener.showProgress();
            isLoadingNow = true;
            if (mState == TicketStates.MY_TICKET || categoryIds.isEmpty()) {
                App.apiManager.getTicketsByState(mState, offset, Const.DEFAULT_AMOUNT);
            } else {
                App.apiManager.getTicketsByStateAndCategory(mState, offset, Const.DEFAULT_AMOUNT,
                        categoryIds.toArray(new Long[categoryIds.size()]));
            }
        }
    }

    private List<Ticket> getDrafts() {
        return App.dataManager.getDraftTickets(mState.getStates(), App.spManager.getUserId());
    }

    public void onEvent(final TicketsByStateEvent event) {
        if (event.getData() == mState) {
            removeStickyEvent(event);
            mSwipeRefreshLayout.setRefreshing(false);
            if (event.isSuccess()) {
                isAllData = event.getResponseCount() < Const.DEFAULT_AMOUNT;
                if (mState == TicketStates.MY_TICKET) {
                    mTicketsList.clear();
                    mTicketsList.addAll(App.dataManager.getTicketsByUserId(App.spManager.getUserId()));
                } else {
                    if (event.getOffset() == 0) {
                        mTicketsList.clear();
                    }
                    mTicketsList.addAll(event.getTicketList());
                }
                notifyDataSetChanged();
            }
        }
        isLoadingNow = false;
        mListener.hideProgress();
    }

    private boolean isDraftMode() {
        return mState != null && mState.containsStatus(Const.TICKET_STATUS_DRAFT);
    }

    @Override
    public void onListItemClick(final int position) {
        if (mActionMode == null) {
            navigateToTicketActivity(mActivity, mTicketsList.get(position).getId(), mState, false);
        } else {
            toggleSelection(position);
        }
    }

    private void navigateToTicketActivity(final Activity activity, final long id,
                                          final TicketStates state, final boolean isFromMap) {
        Intent intent;
        if (state != null && state.containsStatus(Const.TICKET_STATUS_DRAFT)) {
            intent = new Intent(activity, NewTicketActivity.class);
            intent.putExtra(TicketActivity.ID, id);
        } else {
            intent = TicketActivity.newIntent(getContext(), id, false, isFromMap);
        }
        activity.startActivityForResult(intent, MainActivity.REQUEST_NEW_TICKET);
    }

    @Override
    public boolean onListItemLongClick(final int position) {
        if (mActionMode == null && isDraftMode()) {
            startActionMode();
            toggleSelection(position);
            return true;
        }
        return false;
    }

    /**
     * Toggle the selection state of an item.
     * <p/>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (mActionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */

    private void toggleSelection(final int position) {
        mTicketsAdapter.toggleSelection(position);
        int count = mTicketsAdapter.getSelectedItemCount();
        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    private void startActionMode() {
        mActionMode = mListener.startActionMode(mActionModeCallback);
        mListener.hideActionBar();
    }

    public void onEvent(final ErrorApiEvent errorApiEvent) {
        removeStickyEvent(errorApiEvent);
        if (errorApiEvent.getErrorResponse() != null
                && errorApiEvent.getErrorResponse().getCode() == LikeTask.STATUS_LIKED) {
            return;
        }
        if (!TextUtils.isEmpty(errorApiEvent.getMessage())) {
            Toaster.share(mView, errorApiEvent.getMessage());
        }
        if (errorApiEvent.getErrorResponse() != null && errorApiEvent.getErrorResponse().getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            mListener.logoutWithoutApi();
        }
    }

    /**
     * Action mode handling callback
     */
    public class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
            return false;
        }


        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    List<Integer> selectedItems = mTicketsAdapter.getSelectedItems();
                    List<Long> selectedIds = new ArrayList<>();
                    for (Integer position : selectedItems) {
                        selectedIds.add(mTicketsList.get(position).getId());
                    }
                    App.dataManager.deleteTicketById(selectedIds);
                    updateList();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(final ActionMode mode) {
            mTicketsAdapter.clearSelection();
            mActionMode = null;
            mListener.showActionBar();
        }

    }
}
