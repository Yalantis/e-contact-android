package ua.gov.dp.econtact.interfaces;

import android.support.v7.view.ActionMode;

import ua.gov.dp.econtact.fragment.ticket.TicketsListFragment;

/**
 * Created by Aleksandr on 09.09.2015.
 */
public interface TicketListFragmentListener {

    ActionMode startActionMode(final TicketsListFragment.ActionModeCallback callback);

    void showProgress();

    void hideProgress();

    void showActionBar();

    void hideActionBar();

    void logoutWithoutApi();
}
