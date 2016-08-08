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
public class District extends RealmObject implements Parcelable {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DISTRICT = "district";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String title;
    @SerializedName(DISTRICT)
    private DistrictFake district;

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

    public DistrictFake getDistrict() {
        return district;
    }

    public void setDistrict(final DistrictFake district) {
        this.district = district;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeParcelable(this.district, flags);
    }

    public District() {
    }

    protected District(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.district = in.readParcelable(DistrictFake.class.getClassLoader());
    }

    public static final Parcelable.Creator<District> CREATOR = new Parcelable.Creator<District>() {
        @Override
        public District createFromParcel(Parcel source) {
            return new District(source);
        }

        @Override
        public District[] newArray(int size) {
            return new District[size];
        }
    };
}
