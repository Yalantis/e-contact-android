package ua.gov.dp.econtact.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 8/22/15.
 *
 * @author Ed
 */
public class StreetType extends RealmObject implements Parcelable {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CITY_ID = "cityId";
    public static final String SHORT_NAME = "short_name";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String name;
    @SerializedName(SHORT_NAME)
    private String shortName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
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
        dest.writeString(this.shortName);
    }

    public StreetType() {
    }

    protected StreetType(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.shortName = in.readString();
    }

    public static final Parcelable.Creator<StreetType> CREATOR = new Parcelable.Creator<StreetType>() {
        @Override
        public StreetType createFromParcel(Parcel source) {
            return new StreetType(source);
        }

        @Override
        public StreetType[] newArray(int size) {
            return new StreetType[size];
        }
    };
}
