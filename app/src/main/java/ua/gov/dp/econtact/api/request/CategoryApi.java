package ua.gov.dp.econtact.api.request;

import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.model.dto.CategoryListDTO;

import retrofit.Callback;
import retrofit.http.GET;

public interface CategoryApi {

    @GET(ApiSettings.URL.CATEGORY_ALL)
    void getCategories(Callback<CategoryListDTO> callback);
}
