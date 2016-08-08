package ua.gov.dp.econtact.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 7/26/15.
 *
 * @author Andrew Khristyan
 */
public class House extends RealmObject implements Parcelable {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String STREET_ID = "streetId";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String name;
    @SerializedName(STREET_ID)
    private long streetId;

    public long getStreetId() {
        return streetId;
    }

    public void setStreetId(final long streetId) {
        this.streetId = streetId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.streetId);
    }

    public House() {
    }

    protected House(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.streetId = in.readLong();
    }

    public static final Parcelable.Creator<House> CREATOR = new Parcelable.Creator<House>() {
        @Override
        public House createFromParcel(Parcel source) {
            return new House(source);
        }

        @Override
        public House[] newArray(int size) {
            return new House[size];
        }
    };
}
