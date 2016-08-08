package ua.gov.dp.econtact.api.task.category;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.request.CategoryApi;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.model.dto.CategoryListDTO;

import retrofit.client.Response;

/**
 * Get user by id
 */
public class GetAllCategoryTask extends ApiTask<CategoryApi, CategoryListDTO> {

    public GetAllCategoryTask(final CategoryApi api) {
        super(api, null);
    }

    @Override
    public void run() {
        api.getCategories(this);
    }

    @Override
    public void onSuccess(final CategoryListDTO categoryList, final Response response) {
        //TODO check and store version
        App.dataManager.saveCategoriesFromServerData(categoryList);

    }
}
