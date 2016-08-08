package ua.gov.dp.econtact.model;

import com.google.gson.annotations.SerializedName;
import ua.gov.dp.econtact.model.address.Address;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Andrew Khristyan
 * 7/25/15.
 */
public class User extends RealmObject {

    public static final String ID = "id";
    public static final String EMAIL = "email";
    public static final String BIRTHDAY = "birthday";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";

    public static final String LAST_NAME = "last_name";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String FB_REGISTER = "fb_registered";

    public static final String IMAGE = "image";
    public static final String SOCIAL_CONDITION = "social_condition";
    public static final String FACILITIES = "facilities";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(SOCIAL_CONDITION)
    private SocialCondition socialCondition;
    @SerializedName(FACILITIES)
    private Facilities facilities;
    @SerializedName(EMAIL)
    private String email;
    @SerializedName(BIRTHDAY)
    private long birthdaySeconds;
    @SerializedName(FIRST_NAME)
    private String firstName;
    @SerializedName(LAST_NAME)
    private String lastName;
    @SerializedName(MIDDLE_NAME)
    private String middleName;
    @SerializedName(ADDRESS)
    private Address address;
    @SerializedName(IMAGE)
    private String image;
    @SerializedName(PHONE)
    private String phone;
    @SerializedName(FB_REGISTER)
    private int fbRegistered = 0;

    public int getFbRegistered() {
        return fbRegistered;
    }

    public void setFbRegistered(final int fbRegistered) {
        this.fbRegistered = fbRegistered;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getBirthdaySeconds() {
        return birthdaySeconds;
    }

    public void setBirthdaySeconds(final long birthdaySeconds) {
        this.birthdaySeconds = birthdaySeconds;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String user) {
        this.email = user;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public SocialCondition getSocialCondition() {
        return socialCondition;
    }

    public void setSocialCondition(final SocialCondition socialCondition) {
        this.socialCondition = socialCondition;
    }

    public Facilities getFacilities() {
        return facilities;
    }

    public void setFacilities(final Facilities facilities) {
        this.facilities = facilities;
    }
}
