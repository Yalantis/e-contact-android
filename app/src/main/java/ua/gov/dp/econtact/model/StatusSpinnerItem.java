package ua.gov.dp.econtact.model;

/**
 * Created by Kirill-Penzykov
 * 02.09.2015.
 */
public class StatusSpinnerItem {
    private String itemName;
    private TicketStates state;

    public StatusSpinnerItem(final String itemName, final TicketStates state) {
        this.itemName = itemName;
        this.state = state;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(final String itemName) {
        this.itemName = itemName;
    }

    public TicketStates getState() {
        return state;
    }

    public void setState(final TicketStates state) {
        this.state = state;
    }
}
