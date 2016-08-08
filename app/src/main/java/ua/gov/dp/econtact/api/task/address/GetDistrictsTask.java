package ua.gov.dp.econtact.api.task.address;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.AddressApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.event.address.DistrictEvent;
import ua.gov.dp.econtact.model.address.District;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.client.Response;

/**
 * Get tickets
 */
public class GetDistrictsTask extends ApiTask<AddressApi, List<District>> {

    public GetDistrictsTask(final AddressApi api) {
        super(api, null);
    }

    @Override
    public void run() {
        api.getDistricts(this);
    }

    @Override
    public void onSuccess(final List<District> districts, final Response response) {
        App.dataManager.saveDistrictsFromServerData(districts);
        EventBus.getDefault().postSticky(new DistrictEvent());
    }
}
