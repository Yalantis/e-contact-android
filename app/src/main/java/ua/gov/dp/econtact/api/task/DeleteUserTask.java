package ua.gov.dp.econtact.api.task;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.model.dto.GetUserDTO;

import retrofit.client.Response;

/**
 * Get user by id
 */
public class DeleteUserTask extends ApiTask<UserApi, GetUserDTO> {

    private long mUserId;

    public DeleteUserTask(final UserApi api, final long userId) {
        super(api, null);
        mUserId = userId;
    }

    @Override
    public void run() {
        api.deleteUserById(mUserId, this);
    }

    @Override
    public void onSuccess(final GetUserDTO getProfileDTO, final Response response) {
        App.dataManager.saveProfileFromServerData(getProfileDTO.getUser());
    }
}
