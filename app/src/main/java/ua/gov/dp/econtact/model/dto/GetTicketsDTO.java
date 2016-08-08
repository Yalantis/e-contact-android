package ua.gov.dp.econtact.model.dto;

import ua.gov.dp.econtact.model.Ticket;

import java.util.List;

public class GetTicketsDTO {

    private List<Ticket> tickets;

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(final List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
