package ua.gov.dp.econtact.manager;

import android.content.Context;

import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.category.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yalantis
 */
public class CacheManager implements Manager {

    private User mUser;
    private List<Ticket> mTickets;

    private String mEmail;
    private String mFirstName;
    private String mMiddleName;
    private String mLastName;
    private String mPassword;
    private Address mAddress;
    private boolean isStartPhoneValidate;

    public void setSignUpInf(final String email, final String firstName, final String middleName,
                             final String lastName, final String password, final Address address) {
        mEmail = email;
        mFirstName = firstName;
        mMiddleName = middleName;
        mLastName = lastName;
        mPassword = password;
        mAddress = address;
    }

    @Override
    public void init(final Context context) {
        mTickets = new ArrayList<>();
    }

    public void setUser(final User user) {
        mUser = user;
    }

    public void setTickets(final List<Ticket> tickets) {
        mTickets.addAll(tickets);
    }

    public List<Ticket> getTickets() {
        return mTickets;
    }

    public User getCurrentUser() {
        return mUser;
    }

    @Override
    public void clear() {
        mUser = null;
    }

    public void setCategories(final List<Category> categories) {
    }

    public String getEmail() {
        return mEmail;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPassword() {
        return mPassword;
    }

    public Address getAddress() {
        return mAddress;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public boolean isStartPhoneValidate() {
        return isStartPhoneValidate;
    }

    public void setStartPhoneValidate(boolean startPhoneValidate) {
        isStartPhoneValidate = startPhoneValidate;
    }
}
