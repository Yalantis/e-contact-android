package ua.gov.dp.econtact.api.task.address;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.AddressApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.model.address.House;

import java.util.List;

import retrofit.client.Response;

/**
 * Get tickets
 */
public class GetHouseTask extends ApiTask<AddressApi, List<House>> {
    private final long id;

    public GetHouseTask(final AddressApi api, final long id) {
        super(api, null);
        this.id = id;
    }

    @Override
    public void run() {
        api.getHousesByStreet(id, this);
    }

    @Override
    public void onSuccess(final List<House> houses, final Response response) {
        for (House house : houses) {
            house.setStreetId(id);
        }
        App.dataManager.saveHousesFromServerData(houses);
    }
}
