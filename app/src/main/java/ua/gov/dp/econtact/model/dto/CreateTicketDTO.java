package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

import ua.gov.dp.econtact.model.GeoAddress;
import ua.gov.dp.econtact.model.State;
import ua.gov.dp.econtact.model.Type;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.category.Category;

public class CreateTicketDTO {

    private Long id;
    private User user;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("created_date")
    private long created;
    private State state;
    @SerializedName("ticket_id")
    private String ticketId;
    private String image;
    private Address address;
    private Category category;
    private Type type;
    @SerializedName("geo_address")
    private GeoAddress geoAddress;

    public GeoAddress getGeoAddress() {
        return geoAddress;
    }

    public void setGeoAddress(final GeoAddress geoAddress) {
        this.geoAddress = geoAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public long getId() {
        return id == null ? 0 : id;
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

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(final long created) {
        this.created = created;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(final String ticketId) {
        this.ticketId = ticketId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }
}
