package ua.gov.dp.econtact.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 8/7/15.
 *
 * @author Ed Baev
 */
public class Manager extends RealmObject {

    public static final String ID = "id";
    public static final String NAME = "name";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String name;

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
