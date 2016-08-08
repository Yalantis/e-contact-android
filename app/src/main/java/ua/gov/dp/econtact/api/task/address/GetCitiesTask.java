package ua.gov.dp.econtact.api.task.address;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.AddressApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.model.address.City;

import java.util.List;

import retrofit.client.Response;

/**
 * Get tickets
 */
public class GetCitiesTask extends ApiTask<AddressApi, List<City>> {
    private final long mId;

    public GetCitiesTask(final AddressApi api, final long id) {
        super(api, null);
        mId = id;
    }

    @Override
    public void run() {
        api.getCitiesByDistrictId(mId, this);
    }

    @Override
    public void onSuccess(final List<City> cities, final Response response) {
        for (City city : cities) {
            city.setDistrictId(mId);
        }
        App.dataManager.saveCitiesFromServerData(cities);
    }
}
