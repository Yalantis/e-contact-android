package ua.gov.dp.econtact.api.task.ticket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.event.tickets.TicketCreatedEvent;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.dto.CreateTicketDTO;

import java.util.ArrayList;
import java.util.Collections;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Create tickets
 */
public class CreateTicketTask extends ApiTask<TicketApi, Ticket> {
    private CreateTicketDTO createTicketDTO;

    public CreateTicketTask(final TicketApi api, final CreateTicketDTO createTicketDTO) {
        super(api, null);
        this.createTicketDTO = createTicketDTO;
    }

    @Override
    public void run() {
        Gson gson;
        try {
            gson = App.apiManager.getGsonBuilder().create();
        } catch (ClassNotFoundException e) {
            gson = new Gson();
        }

        JsonElement element = gson.toJsonTree(createTicketDTO);
        api.createTicket(element, this);
    }

    @Override
    public void onSuccess(final Ticket ticket, final Response response) {
        App.dataManager.saveTicketsToDB(new ArrayList<>(Collections.singletonList(ticket)));
        EventBus.getDefault().postSticky(new TicketCreatedEvent(ticket));
    }
}
