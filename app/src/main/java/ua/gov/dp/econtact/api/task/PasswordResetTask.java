package ua.gov.dp.econtact.api.task;

import retrofit.client.Response;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.event.PasswordResetEvent;
import ua.gov.dp.econtact.model.dto.PasswordResetDTO;

/**
 * Created by cleanok on 26.05.16.
 */
public class PasswordResetTask extends ApiTask<UserApi, Void> {
    public static final int CODE_USER_NOT_FOUND = 404;
    public static final int CODE_INVALID_EMAIL = 403;
    private final String mEmail;

    public PasswordResetTask(UserApi userApi, String email) {
        super(userApi);
        mEmail = email;
    }

    @Override
    public void onSuccess(Void aVoid, Response response) {
        App.eventBus.postSticky(new PasswordResetEvent(mEmail));
    }

    @Override
    public void run() {
        PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setEmail(mEmail);
        api.resetPassword(passwordResetDTO, this);
    }
}
