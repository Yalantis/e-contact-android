package ua.gov.dp.econtact.model.category;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 7/26/15.
 *
 * @author Ed Baev
 */
public class Category extends RealmObject {

    public static final String ID = "id";
    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName("name")
    private String name;

    public Category(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Category() {
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }


}
