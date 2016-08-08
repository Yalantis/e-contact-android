package ua.gov.dp.econtact.api.task.address;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.AddressApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.model.address.Street;

import java.util.List;

import retrofit.client.Response;

/**
 * Get tickets
 */
public class GetStreetTask extends ApiTask<AddressApi, List<Street>> {
    private final long id;

    public GetStreetTask(final AddressApi api, final long id) {
        super(api, null);
        this.id = id;
    }

    @Override
    public void run() {
        api.getStreetsByCity(id, this);
    }

    @Override
    public void onSuccess(final List<Street> streets, final Response response) {
        for (Street street : streets) {
            street.setCityId(id);
        }
        App.dataManager.saveStreetFromServerData(streets);
    }
}
