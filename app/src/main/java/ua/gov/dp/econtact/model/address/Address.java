package ua.gov.dp.econtact.model.address;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 7/25/15.
 *
 * @author Andrew Khristyan
 */
public class Address extends RealmObject implements Parcelable {

    public static final String ID = "id";
    public static final String DISTRICT = "district";
    public static final String CITY = "city";
    public static final String STREET = "street";
    public static final String HOUSE = "house";
    public static final String FLAT = "flat";

    @PrimaryKey
    @SerializedName(ID)
    private long id;

    @SerializedName(DISTRICT)
    private District district;

    @SerializedName(CITY)
    private City city;

    @SerializedName(STREET)
    private Street street;

    @SerializedName(HOUSE)
    private House house;

    @SerializedName(FLAT)
    private String flat;

    public static String generateAddressLabel(final Address address) {
        if (address == null) {
            return "";
        }
        String addressLabel = generateAddressLabelWithoutFlat(address);
        if (!TextUtils.isEmpty(address.getFlat())) {
            addressLabel += ", " + address.getFlat();
        }
        return addressLabel;
    }

    public static String generateAddressLabelWithoutFlat(final Address address) {
        if (address == null) {
            return "";
        }
        String addressLabel = address.getCity() == null || address.getStreet() == null ? "" :
                address.getCity().getTitle() + ", " + address.getStreet().getName();
        if (address.getHouse() != null && !TextUtils.isEmpty(address.getHouse().getName())) {
            addressLabel += ", " + address.getHouse().getName();
        }
        return addressLabel;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(final String flat) {
        this.flat = flat;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(final District district) {
        this.district = district;
    }

    public City getCity() {
        return city;
    }

    public void setCity(final City city) {
        this.city = city;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(final Street street) {
        this.street = street;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(final House house) {
        this.house = house;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.district, flags);
        dest.writeParcelable(this.city, flags);
        dest.writeParcelable(this.street, flags);
        dest.writeParcelable(this.house, flags);
        dest.writeString(this.flat);
    }

    public Address() {
    }

    protected Address(Parcel in) {
        this.id = in.readLong();
        this.district = in.readParcelable(District.class.getClassLoader());
        this.city = in.readParcelable(City.class.getClassLoader());
        this.street = in.readParcelable(Street.class.getClassLoader());
        this.house = in.readParcelable(House.class.getClassLoader());
        this.flat = in.readString();
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}

