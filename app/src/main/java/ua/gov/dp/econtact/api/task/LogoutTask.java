package ua.gov.dp.econtact.api.task;

import retrofit.client.Response;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.UserApi;

/**
 * Created by cleanok on 30.05.16.
 */
public class LogoutTask extends ApiTask<UserApi, Void> {
    public LogoutTask(UserApi api) {
        super(api);
    }

    @Override
    public void onSuccess(Void aVoid, Response response) {
        App.clearSession();
    }

    @Override
    public void run() {
        api.logout(this);
    }
}
