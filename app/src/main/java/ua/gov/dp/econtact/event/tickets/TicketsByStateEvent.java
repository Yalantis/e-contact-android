package ua.gov.dp.econtact.event.tickets;

import ua.gov.dp.econtact.event.ApiEvent;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketStates;

import java.util.List;

public class TicketsByStateEvent extends ApiEvent<TicketStates> {
    private int mOffset;
    private int mResponseCount;
    private List<Ticket> mTicketList;
    private boolean isSuccess;

    public TicketsByStateEvent(final TicketStates states, final int offset,
                               final int responseCount, final List<Ticket> tickets) {
        super(states);
        mOffset = offset;
        mResponseCount = responseCount;
        mTicketList = tickets;
        isSuccess = true;
    }

    public TicketsByStateEvent(final TicketStates states, final int offset, final boolean success) {
        super(states);
        mOffset = offset;
        isSuccess = success;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public List<Ticket> getTicketList() {
        return mTicketList;
    }

    public int getResponseCount() {
        return mResponseCount;
    }

    public int getOffset() {
        return mOffset;
    }
}
