package ua.gov.dp.econtact.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 8/18/15.
 *
 * @author Ed
 */
public class CityDistrict extends RealmObject implements Parcelable {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DISTRICT_ID = "districtId";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String title;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeLong(this.districtId);
    }

    public CityDistrict() {
    }

    protected CityDistrict(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.districtId = in.readLong();
    }

    public static final Parcelable.Creator<CityDistrict> CREATOR = new Parcelable.Creator<CityDistrict>() {
        @Override
        public CityDistrict createFromParcel(Parcel source) {
            return new CityDistrict(source);
        }

        @Override
        public CityDistrict[] newArray(int size) {
            return new CityDistrict[size];
        }
    };
}
