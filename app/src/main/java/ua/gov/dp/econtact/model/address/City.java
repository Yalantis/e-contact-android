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
public class City extends RealmObject implements Parcelable {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NAME_RU = "ru_name";
    public static final String DISTRICT_ID = "districtId";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String title;
    @SerializedName(NAME_RU)
    private String nameRu;
    @SerializedName(DISTRICT_ID)
    private long districtId;

    public long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(final long districtId) {
        this.districtId = districtId;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(final String nameRu) {
        this.nameRu = nameRu;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.nameRu);
        dest.writeLong(this.districtId);
    }

    public City() {
    }

    protected City(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.nameRu = in.readString();
        this.districtId = in.readLong();
    }

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
