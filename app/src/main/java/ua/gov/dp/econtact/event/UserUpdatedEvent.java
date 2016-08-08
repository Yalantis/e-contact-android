package ua.gov.dp.econtact.event;

/**
 * Created by Yalantis
 * 02.10.2015.
 *
 * @author Aleksandr
 */
public class UserUpdatedEvent extends ApiEvent<Void> {

    public UserUpdatedEvent(final Void data) {
        super(data);
    }
}
