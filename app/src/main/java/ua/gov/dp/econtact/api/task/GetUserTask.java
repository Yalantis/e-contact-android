package ua.gov.dp.econtact.api.task;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.model.User;

import retrofit.client.Response;

/**
 * Get user by id
 */
public class GetUserTask extends ApiTask<UserApi, User> {

    private long mUserId;

    public GetUserTask(final UserApi api, final long userId) {
        super(api, null);
        mUserId = userId;
    }

    @Override
    public void run() {
        api.getUser(mUserId, this);
    }

    @Override
    public void onSuccess(final User getProfileDTO, final Response response) {
        App.dataManager.saveProfileFromServerData(getProfileDTO);
        // EventBus.getDefault().postSticky(new LoginEvent(String.valueOf(loginSuccessDTO.getId())));
    }
}
