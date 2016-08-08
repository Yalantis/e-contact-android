package ua.gov.dp.econtact.event;

import java.util.List;

import ua.gov.dp.econtact.model.Ticket;

/**
 * Created by cleanok on 30.05.16.
 */
public class TicketsByIdsEvent implements BaseEvent {
    private List<Ticket> mTickets;

    public TicketsByIdsEvent(List<Ticket> tickets) {
        mTickets = tickets;
    }

    public List<Ticket> getTickets() {
        return mTickets;
    }
}
