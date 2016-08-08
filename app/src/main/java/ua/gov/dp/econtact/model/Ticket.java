package ua.gov.dp.econtact.model;

import com.google.gson.annotations.SerializedName;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.category.Category;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ua.gov.dp.econtact.model.category.CategoryWithImages;

/**
 * Created by Andrew Khristyan
 * 7/25/15.
 */
public class Ticket extends RealmObject {
    public static final String ID = "id";

    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName("user")
    private User user;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("created_date")
    private long created;
    @SerializedName("completed_date")
    private long completed;
    @SerializedName("start_date")
    private long startDate;
    @SerializedName("state")
    private State state;
    @SerializedName("manager")
    private Manager manager;
    @SerializedName("ticket_id")
    private String ticketId;
    @SerializedName("address")
    private Address address;
    @SerializedName("comment")
    private String comment;

    private Category category;

    private Type type;

    @SerializedName("geo_address")
    private GeoAddress geoAddress;
    @SerializedName("likes_counter")
    private int likesCounter;
    @SerializedName("answers")
    private RealmList<TicketAnswer> answers;
    @SerializedName("files")
    private RealmList<TicketFiles> files;
    @SerializedName("performers")
    private RealmList<Performer> performers;
    @SerializedName("deadline")
    private long deadline;

    public int getLikesCounter() {
        return likesCounter;
    }

    public void setLikesCounter(final int likesCounter) {
        this.likesCounter = likesCounter;
    }

    public GeoAddress getGeoAddress() {
        return geoAddress;
    }

    public void setGeoAddress(final GeoAddress geoAddress) {
        this.geoAddress = geoAddress;
    }

    public RealmList<TicketFiles> getFiles() {
        return files;
    }

    public void setFiles(final RealmList<TicketFiles> files) {
        this.files = files;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(final long startDate) {
        this.startDate = startDate;
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
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
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

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(final long completed) {
        this.completed = completed;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(final Manager manager) {
        this.manager = manager;
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

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public RealmList<TicketAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(final RealmList<TicketAnswer> answers) {
        this.answers = answers;
    }

    public RealmList<Performer> getPerformers() {
        return performers;
    }

    public void setPerformers(final RealmList<Performer> performers) {
        this.performers = performers;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(final long deadline) {
        this.deadline = deadline;
    }
}
