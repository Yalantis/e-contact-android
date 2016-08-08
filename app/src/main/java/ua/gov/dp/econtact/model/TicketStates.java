package ua.gov.dp.econtact.model;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;

/**
 * Created by Yalantis
 * 7/26/15.
 */
public enum TicketStates {

    IN_PROGRESS(new long[]{0, 9, 5, 7, 8}, true, R.drawable.state_processing_shape),
    DONE(new long[]{10, 6}, true, R.drawable.state_done_shape),
    PENDING(new long[]{1, 3, 4}, true, R.drawable.state_pending_shape),
    MODERATION(new long[]{Const.TICKET_STATUS_MODERATION}, true, R.drawable.state_pending_shape),
    DRAFT(new long[]{Const.TICKET_STATUS_DRAFT}, false, R.drawable.state_pending_shape),
    MY_TICKET(new long[]{1, Const.TICKET_STATUS_MODERATION}, true, R.drawable.state_processing_shape),
    REJECTED(new long[]{2, Const.TICKET_STATUS_REJECTED}, true, R.drawable.state_processing_shape);

    private final long[] states;
    private final boolean backend;
    private final int stateDrawable;

    TicketStates(final long[] states, final boolean backend, final int stateDrawable) {
        this.states = states;
        this.backend = backend;
        this.stateDrawable = stateDrawable;
    }

    public int getStateDrawable() {
        return stateDrawable;
    }

    public long[] getStates() {
        return states;
    }

    public boolean isBackend() {
        return backend;
    }

    public static TicketStates getTicketStateById(final long id) {
        for (TicketStates ticketStates : values()) {
            for (Long stateId : ticketStates.getStates()) {
                if (id == stateId) {
                    return ticketStates;
                }
            }
        }
        return DRAFT;
    }

    public boolean containsStatus(final long status) {
        for (long s : states) {
            if (s == status) {
                return true;
            }
        }
        return false;
    }
}
