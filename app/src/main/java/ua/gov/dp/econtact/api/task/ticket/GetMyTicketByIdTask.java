package ua.gov.dp.econtact.api.task.ticket;

import retrofit.client.Response;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.event.GotTicketEvent;
import ua.gov.dp.econtact.model.Ticket;

/**
 * Created by Alexey on 22.06.2016.
 */
public class GetMyTicketByIdTask extends ApiTask<TicketApi, Ticket> {

    private final long mTicketId;

    public GetMyTicketByIdTask(TicketApi ticketApi, long ticketId) {
        super(ticketApi);
        mTicketId = ticketId;
    }

    @Override
    public void onSuccess(Ticket ticket, Response response) {
        App.eventBus.postSticky(new GotTicketEvent(ticket));
    }

    @Override
    public void run() {
        api.getMyTicketById(mTicketId, this);
    }
}
