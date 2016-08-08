package ua.gov.dp.econtact.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 8/7/15.
 *
 * @author Ed Baev
 */
public class GeoAddress extends RealmObject {

    public static final String ID = "id";
    public static final String ADDRESS = "address";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    public static String generateAddressLabel(final GeoAddress geoAddress) {
        return TextUtils.isEmpty(geoAddress.getAddress())
                ? geoAddress.getLatitude() + ", " + geoAddress.getLongitude()
                : geoAddress.getAddress();
    }

    @PrimaryKey
    @SerializedName(ID)
    private long id;

    @SerializedName(ADDRESS)
    private String address;

    @SerializedName(LATITUDE)
    private String latitude;

    @SerializedName(LONGITUDE)
    private String longitude;

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(final String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(final String longitude) {
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
