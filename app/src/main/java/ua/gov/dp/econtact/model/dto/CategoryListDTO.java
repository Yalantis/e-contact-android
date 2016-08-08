package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;
import ua.gov.dp.econtact.model.category.Category;

import io.realm.RealmList;
import io.realm.RealmObject;
import ua.gov.dp.econtact.model.category.CategoryWithImages;

/**
 * Created by cleanok on 13.05.16.
 */
public class CategoryListDTO extends RealmObject {

    @SerializedName("categories")
    private RealmList<CategoryWithImages> mCategories;
    @SerializedName("version")
    private int mVersion;

    public RealmList<CategoryWithImages> getCategories() {
        return mCategories;
    }

    public void setCategories(RealmList<CategoryWithImages> categories) {
        mCategories = categories;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        mVersion = version;
    }
}
