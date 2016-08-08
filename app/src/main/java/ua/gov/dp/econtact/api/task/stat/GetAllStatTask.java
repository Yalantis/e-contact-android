package ua.gov.dp.econtact.api.task.stat;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.StatApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.model.stat.StatAll;

import retrofit.client.Response;

/**
 * Get user by id
 */
public class GetAllStatTask extends ApiTask<StatApi, StatAll> {


    public GetAllStatTask(final StatApi api) {
        super(api, null);
    }

    @Override
    public void run() {
        api.getStatAll(this);
    }

    @Override
    public void onSuccess(final StatAll statAll, final Response response) {
        App.dataManager.saveStatAllFromServerData(statAll);
        // EventBus.getDefault().postSticky(new LoginEvent(String.valueOf(loginSuccessDTO.getId())));
    }
}
