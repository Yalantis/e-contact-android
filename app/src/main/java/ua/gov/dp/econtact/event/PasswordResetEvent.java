package ua.gov.dp.econtact.event;

/**
 * Created by cleanok on 26.05.16.
 */
public class PasswordResetEvent implements BaseEvent{
    private String mEmail;

    public PasswordResetEvent(String email) {
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }
}
