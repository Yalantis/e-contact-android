package ua.gov.dp.econtact.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Aleksandr
 * 19.10.2015.
 */
public class TicketAnswer extends RealmObject {

    public static final String ID = "id";
    public static final String FILE_NAME = "filename";
    public static final String ORIGIN_NAME = "origin_name";

    @SerializedName(ID)
    private long id;
    @SerializedName(FILE_NAME)
    private String filename;
    @SerializedName(ORIGIN_NAME)
    private String originName;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(final String originName) {
        this.originName = originName;
    }
}
