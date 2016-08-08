package ua.gov.dp.econtact.event.tickets;

import ua.gov.dp.econtact.event.ApiEvent;
import ua.gov.dp.econtact.model.Ticket;

public class TicketCreatedEvent extends ApiEvent<Ticket> {
    public TicketCreatedEvent(final Ticket ticket) {
        super(ticket);
    }
}
