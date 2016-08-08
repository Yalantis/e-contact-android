package ua.gov.dp.econtact.api.task;

import retrofit.client.Response;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.event.GotTicketEvent;
import ua.gov.dp.econtact.model.Ticket;

/**
 * Created by cleanok on 23.05.16.
 */
public class GetTicketByIdTask extends ApiTask<TicketApi, Ticket> {
    private final long mTicketId;

    public GetTicketByIdTask(TicketApi ticketApi, long ticketId) {
        super(ticketApi);
        mTicketId = ticketId;
    }

    @Override
    public void onSuccess(Ticket ticket, Response response) {
        App.eventBus.postSticky(new GotTicketEvent(ticket));
    }

    @Override
    public void run() {
        api.getTicketById(mTicketId, this);
    }
}
