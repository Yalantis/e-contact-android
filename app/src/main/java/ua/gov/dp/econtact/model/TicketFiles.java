package ua.gov.dp.econtact.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ed
 * 8/22/15.
 */
public class TicketFiles extends RealmObject {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String FILE_NAME = "filename";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String name;
    @SerializedName(FILE_NAME)
    private String filename;

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
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
