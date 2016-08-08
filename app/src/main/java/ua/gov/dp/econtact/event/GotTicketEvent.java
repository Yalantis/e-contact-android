package ua.gov.dp.econtact.event;

import ua.gov.dp.econtact.model.Ticket;

/**
 * Created by cleanok on 23.05.16.
 */
public class GotTicketEvent extends ApiEvent<Ticket> {
    public GotTicketEvent(Ticket data) {
        super(data);
    }
}
