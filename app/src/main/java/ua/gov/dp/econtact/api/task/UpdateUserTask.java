package ua.gov.dp.econtact.api.task;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.event.UserUpdatedEvent;
import ua.gov.dp.econtact.model.User;

import retrofit.client.Response;

/**
 * Created by Yalantis
 *
 * @author <a href="mailto:ilya@goabra.com>Iliya Bershadskiy</a>
 * @since 25.07.15
 */
public class UpdateUserTask extends ApiTask<UserApi, Void> {

    private User mUser;

    public UpdateUserTask(final UserApi api, final User user) {
        super(api);
        mUser = user;
    }

    @Override
    public void onSuccess(final Void aVoid, final Response response) {
        App.eventBus.postSticky(new UserUpdatedEvent(null));
    }

    @Override
    public void run() {
        Gson gson;
        try {
            gson = App.apiManager.getGsonBuilder().create();
        } catch (ClassNotFoundException e) {
            gson = new Gson();
        }
        JsonElement element = gson.toJsonTree(mUser);
        api.updateUser(mUser.getId(), element, this);
    }
}
