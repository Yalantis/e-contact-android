package ua.gov.dp.econtact.event.tickets;

        import ua.gov.dp.econtact.event.ApiEvent;
        import ua.gov.dp.econtact.model.dto.TicketLikeDTO;

/**
 * Created by Yalantis
 * 29.09.2015.
 *
 * @author Aleksandr
 */
public class TicketImageEvent extends ApiEvent<TicketLikeDTO> {

    private long mTicketId;

    public TicketImageEvent(final long ticketId) {
        super(null);
        mTicketId = ticketId;
    }

    public long getTicketId() {
        return mTicketId;
    }

    public void setTicketId(final long ticketId) {
        mTicketId = ticketId;
    }
}
