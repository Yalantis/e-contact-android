package ua.gov.dp.econtact.model.stat;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 7/26/15.
 *
 * @author Ed Baev
 */

public class StatAll extends RealmObject {

    public static final int SINGLETON_ID = 1;
    public static final String ID = "id";

    @PrimaryKey
    @SerializedName(ID)
    private int id;
    private int done;
    private int pending;
    @SerializedName("in_progress")
    private int inProgress;

    public int getDone() {
        return done;
    }

    public void setDone(final int done) {
        this.done = done;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(final int pending) {
        this.pending = pending;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(final int inProgress) {
        this.inProgress = inProgress;
    }


    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
