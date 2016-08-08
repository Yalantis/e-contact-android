package ua.gov.dp.econtact.api.task;

import java.util.List;

import retrofit.client.Response;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.event.TicketsByIdsEvent;
import ua.gov.dp.econtact.model.Ticket;

/**
 * Created by cleanok on 30.05.16.
 */
public class GetTicketsByIdsTask extends ApiTask<TicketApi, List<Ticket>> {
    private final String mIds;

    public GetTicketsByIdsTask(TicketApi ticketApi, String ids) {
        super(ticketApi);
        mIds = ids;
    }

    @Override
    public void onSuccess(List<Ticket> tickets, Response response) {
        App.eventBus.postSticky(new TicketsByIdsEvent(tickets));
    }

    @Override
    public void run() {
        api.getTicketsByIds(mIds, this);
    }
}
