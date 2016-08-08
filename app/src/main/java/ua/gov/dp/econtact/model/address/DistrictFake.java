package ua.gov.dp.econtact.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 05.10.2015.
 *
 * @author Aleksandr
 */
public class DistrictFake extends RealmObject implements Parcelable {

    public static final String ID = "id";
    public static final String NAME = "name";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String title;

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
    }

    public DistrictFake() {
    }

    protected DistrictFake(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<DistrictFake> CREATOR = new Parcelable.Creator<DistrictFake>() {
        @Override
        public DistrictFake createFromParcel(Parcel source) {
            return new DistrictFake(source);
        }

        @Override
        public DistrictFake[] newArray(int size) {
            return new DistrictFake[size];
        }
    };
}
