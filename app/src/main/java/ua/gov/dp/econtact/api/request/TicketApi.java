package ua.gov.dp.econtact.api.request;

import com.google.gson.JsonElement;
import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.model.SmallTicket;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.dto.TicketLikeDTO;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author <a href="mailto:iBersh20@gmail.com>Iliya Bershadskiy</a>
 * @since 25.07.15
 */
public interface TicketApi {
    @GET(ApiSettings.URL.TICKETS)
    void getTickets(@Query("offset") int offset, @Query("amount") int amount, Callback<List<Ticket>> callback);

    @GET(ApiSettings.URL.TICKETS +"?model_size=small")
    void getTicketsSmall(Callback<List<SmallTicket>> callback);

    @GET(ApiSettings.URL.TICKETS)
    void getTicketsByState(@Query("offset") int offset, @Query("amount") int amount,
                           @Query("state") String state, Callback<List<Ticket>> callback);

    @GET(ApiSettings.URL.TICKETS)
    void getTicketsByState(@Query("offset") int offset, @Query("amount") int amount, @Query("state") String state,
                           @Query("category") String category, Callback<List<Ticket>> callback);

    @POST(ApiSettings.URL.TICKET)
    void createTicket(@Body JsonElement ticketDTO, Callback<Ticket> callback);

    @GET(ApiSettings.URL.TICKET + "/{" + ApiSettings.URL.TICKET_ID + "}")
    void getTicketById(@Path(ApiSettings.URL.TICKET_ID) long ticketId, Callback<Ticket> callback);

    @GET(ApiSettings.URL.MY_TICKETS)
    void getMyTickets(Callback<List<Ticket>> callback);

    @PUT(ApiSettings.URL.LIKE)
    void likeTicket(@Path("ticket_id") long ticketId, @Body JsonElement tokenDTO, Callback<TicketLikeDTO> callback);

    @GET(ApiSettings.URL.TICKETS_BY_IDS)
    void getTicketsByIds(@Query("ticket_ids") String ids, Callback<List<Ticket>> callback);

    @GET(ApiSettings.URL.GET_MY_TICKET_BY_ID)
    void getMyTicketById(@Path(ApiSettings.URL.TICKET_ID) long ticketId, Callback<Ticket> callback);
}
