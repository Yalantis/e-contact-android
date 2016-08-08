package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;
import ua.gov.dp.econtact.model.address.Address;

/**
 * @author <a href="mailto:iBersh20@gmail.com>Iliya Bershadskiy</a>
 * @since 26.07.15
 */
public class UserDto {

    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("middle_name")
    private String middleName;
    private String password;
    private Address address;
    private String phone;

    public UserDto(final String email, final String firstName, final String lastName,
                   final String middleName, final String password, final Address address, final String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.address = address;
        this.middleName = middleName;
        this.phone = phone;
    }

    public UserDto(final String email, final String firstName, final String lastName,
                   final String middleName, final String password, final Address address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.address = address;
        this.middleName = middleName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }
}
