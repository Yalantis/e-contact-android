package ua.gov.dp.econtact.api.task.ticket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.event.tickets.TicketLikedEvent;
import ua.gov.dp.econtact.model.dto.FbTokenDTO;
import ua.gov.dp.econtact.model.dto.TicketLikeDTO;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Created by Yalantis
 * 29.09.2015.
 *
 * @author Aleksandr
 */
public class LikeTask extends ApiTask<TicketApi, TicketLikeDTO> {

    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_LIKED = 422;

    private long ticketId;
    private String fbToken;

    public LikeTask(final TicketApi api, final long ticketId, final String fbToken) {
        super(api);
        this.ticketId = ticketId;
        this.fbToken = fbToken;
    }

    @Override
    public void onSuccess(final TicketLikeDTO dto, final Response response) {
        EventBus.getDefault().postSticky(new TicketLikedEvent(dto));
    }

    @Override
    public void run() {
        Gson gson;
        try {
            gson = App.apiManager.getGsonBuilder().create();
        } catch (ClassNotFoundException e) {
            gson = new Gson();
        }

        JsonElement element = gson.toJsonTree(new FbTokenDTO(fbToken));
        api.likeTicket(ticketId, element, this);
    }
}
