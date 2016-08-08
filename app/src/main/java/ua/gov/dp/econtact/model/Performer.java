package ua.gov.dp.econtact.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yalantis
 * 8/7/15.
 *
 * @author Ed Baev
 */
public class Performer extends RealmObject {

    public static final String ORGANIZATION = "organization";
    public static final String PERSON = "person";
    public static final String ID = "id";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName(PERSON)
    private String person;
    @SerializedName(ORGANIZATION)
    private String organization;

    public Performer(final long id, final String person, final String organization) {
        this.id = id;
        this.person = person;
        this.organization = organization;
    }

    public Performer() {

    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(final String person) {
        this.person = person;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(final String organization) {
        this.organization = organization;
    }
}
