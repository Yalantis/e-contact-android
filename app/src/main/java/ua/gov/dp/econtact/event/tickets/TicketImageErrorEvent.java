package ua.gov.dp.econtact.event.tickets;

import ua.gov.dp.econtact.event.ApiEvent;
import ua.gov.dp.econtact.model.dto.TicketLikeDTO;

/**
 * Created by Aleksandr on 29.09.2015.
 */
public class TicketImageErrorEvent extends ApiEvent<TicketLikeDTO> {

    public TicketImageErrorEvent() {
        super(null);
    }
}
