package ua.gov.dp.econtact.model.address;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 7/26/15
 *
 * @author Andrew Khristyan
 */
public class Street extends RealmObject implements Parcelable {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CITY_ID = "cityId";
    public static final String NAME_RU = "ru_name";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(NAME)
    private String name;
    @SerializedName(NAME_RU)
    private String nameRu;
    @SerializedName(CITY_ID)
    private long cityId;
    @SerializedName("city_district")
    private CityDistrict cityDistrict;
    @SerializedName("street_type")
    private StreetType streetType;

    public CityDistrict getCityDistrict() {
        return cityDistrict;
    }

    public StreetType getStreetType() {
        return streetType;
    }

    public void setStreetType(final StreetType streetType) {
        this.streetType = streetType;
    }

    public void setCityDistrict(final CityDistrict cityDistrict) {
        this.cityDistrict = cityDistrict;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(final long cityId) {
        this.cityId = cityId;
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
        dest.writeString(this.name);
        dest.writeString(this.nameRu);
        dest.writeLong(this.cityId);
        dest.writeParcelable(this.cityDistrict, flags);
        dest.writeParcelable(this.streetType, flags);
    }

    public Street() {
    }

    protected Street(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.nameRu = in.readString();
        this.cityId = in.readLong();
        this.cityDistrict = in.readParcelable(CityDistrict.class.getClassLoader());
        this.streetType = in.readParcelable(StreetType.class.getClassLoader());
    }

    public static final Parcelable.Creator<Street> CREATOR = new Parcelable.Creator<Street>() {
        @Override
        public Street createFromParcel(Parcel source) {
            return new Street(source);
        }

        @Override
        public Street[] newArray(int size) {
            return new Street[size];
        }
    };
}
