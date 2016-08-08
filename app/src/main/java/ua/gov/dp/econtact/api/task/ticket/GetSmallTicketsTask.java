package ua.gov.dp.econtact.api.task.ticket;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;
import ua.gov.dp.econtact.event.tickets.SmallTicketsEvent;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.model.SmallTicket;

/**
 * Created by cleanok on 30.05.16.
 */
public class GetSmallTicketsTask extends ApiTask<TicketApi, List<SmallTicket>> {
    public GetSmallTicketsTask(TicketApi ticketApi) {
        super(ticketApi);
    }

    @Override
    public void onSuccess(List<SmallTicket> tickets, Response response) {
        EventBus.getDefault().postSticky(new SmallTicketsEvent(tickets));

    }

    @Override
    public void run() {
        api.getTicketsSmall(this);
    }
}
