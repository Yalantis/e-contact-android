package ua.gov.dp.econtact.api.request;

import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.model.stat.StatAll;

import retrofit.Callback;
import retrofit.http.GET;

public interface StatApi {

    @GET(ApiSettings.URL.STAT_ALL)
    void getStatAll(Callback<StatAll> callback);
}
