package ua.gov.dp.econtact.event.tickets;

import ua.gov.dp.econtact.event.ApiEvent;
import ua.gov.dp.econtact.model.dto.TicketLikeDTO;

/**
 * Created by Yalantis
 * 29.09.2015.
 *
 * @author Aleksandr
 */
public class TicketLikedEvent extends ApiEvent<TicketLikeDTO> {

    public TicketLikedEvent(final TicketLikeDTO data) {
        super(data);
    }
}
