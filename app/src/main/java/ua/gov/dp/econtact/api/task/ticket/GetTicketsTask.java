package ua.gov.dp.econtact.api.task.ticket;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.event.tickets.TicketsByStateEvent;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketStates;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetTicketsTask extends ApiTask<TicketApi, List<Ticket>> {
    private int offset;
    private int amount;
    private TicketStates state = TicketStates.MY_TICKET;
    private Long[] categories;

    public GetTicketsTask(final TicketApi api, final int offset, final int amount) {
        super(api, null);
        this.offset = offset;
        this.amount = amount;
    }

    public GetTicketsTask(final TicketApi api, final int offset, final int amount,
                          final TicketStates states) {
        this(api, offset, amount);
        this.state = states;
    }

    public GetTicketsTask(final TicketApi api, final int offset, final int amount,
                          final TicketStates states, final Long[] categories) {
        this(api, offset, amount, states);
        this.categories = categories;
    }

    @Override
    public void run() {
        String stateString = Arrays.toString(state.getStates())
                .replace(" ", "").replace("[", "").replace("]", "");
        switch (state) {
            case IN_PROGRESS:
            case DONE:
            case PENDING:
                if (categories == null) {
                    api.getTicketsByState(offset, amount, stateString, this);
                } else {
                    String categoriesString = Arrays.toString(categories)
                            .replace(" ", "").replace("[", "").replace("]", "");
                    api.getTicketsByState(offset, amount, stateString, categoriesString, this);
                }
                break;
            case MY_TICKET:
                api.getMyTickets(this);
                break;
            default:
                api.getTickets(offset, amount, this);
                break;
        }
    }

    @Override
    public void onSuccess(final List<Ticket> tickets, final Response response) {
        if (offset == 0 && !tickets.isEmpty()) {
            if (state == TicketStates.MY_TICKET) {
                if (App.dataManager.getCurrentUser() != null) {
                    long id = App.spManager.getUserId();
                    App.dataManager.deleteTicketByUserId(id);
                }
            } else {
                App.dataManager.deleteTicketByState(state.getStates());
            }
        }
        if (offset == 0 && tickets.isEmpty()) {
            App.dataManager.deleteTicketByState(state.getStates());
        }
        App.dataManager.saveTicketsToDB(tickets);
        EventBus.getDefault().postSticky(new TicketsByStateEvent(state, offset, tickets.size(), tickets));
    }

    @Override
    protected void onFailure(final RetrofitError error) {
        super.onFailure(error);
        EventBus.getDefault().postSticky(new TicketsByStateEvent(state, offset, false));
    }
}
