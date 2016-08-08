package ua.gov.dp.econtact.api.request;

import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.model.address.City;
import ua.gov.dp.econtact.model.address.District;
import ua.gov.dp.econtact.model.address.House;
import ua.gov.dp.econtact.model.address.Street;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface AddressApi {

    @GET(ApiSettings.URL.DISTRICT_ALL)
    void getDistricts(Callback<List<District>> callback);

    @GET(ApiSettings.URL.CITIES_ALL + "/{district_id}")
    void getCitiesByDistrictId(@Path("district_id") long id, Callback<List<City>> callback);

    @GET(ApiSettings.URL.STREETS_ALL + "/{city_id}")
    void getStreetsByCity(@Path("city_id") long id, Callback<List<Street>> callback);

    @GET(ApiSettings.URL.HOUSES_ALL + "/{street_id}")
    void getHousesByStreet(@Path("street_id") long id, Callback<List<House>> callback);

}
