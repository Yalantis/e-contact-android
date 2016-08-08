package ua.gov.dp.econtact.event.tickets;

import java.util.List;

import ua.gov.dp.econtact.event.BaseEvent;
import ua.gov.dp.econtact.model.SmallTicket;
import ua.gov.dp.econtact.model.Ticket;

/**
 * Created by cleanok on 30.05.16.
 */
public class SmallTicketsEvent implements BaseEvent {
    private List<SmallTicket> mTickets;

    public SmallTicketsEvent(List<SmallTicket> tickets) {
        mTickets = tickets;
    }

    public List<SmallTicket> getTickets() {
        return mTickets;
    }
}
