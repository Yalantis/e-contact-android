package ua.gov.dp.econtact.model.category;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cleanok on 13.05.16.
 */
public class Images extends RealmObject {
    @SerializedName("40")
    private String mSmall;
    @SerializedName("80")
    private String mMedium;
    @SerializedName("120")
    private String mLarge;


    public String getSmall() {
        return mSmall;
    }

    public void setSmall(String small) {
        mSmall = small;
    }

    public String getMedium() {
        return mMedium;
    }

    public void setMedium(String medium) {
        mMedium = medium;
    }

    public String getLarge() {
        return mLarge;
    }

    public void setLarge(String large) {
        mLarge = large;
    }
}
