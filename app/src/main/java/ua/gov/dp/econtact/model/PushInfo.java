package ua.gov.dp.econtact.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aleksandr
 * 21.10.2015.
 */
public class PushInfo implements Parcelable {

    public static final String ALERT = "alert";
    public static final String TYPE = "type";
    public static final String TICKET_ID = "ticket_id";
    public static final String STATUS = "status";

    @SerializedName(ALERT)
    private String alert;
    @SerializedName(TYPE)
    private int type;
    @SerializedName(TICKET_ID)
    private int ticketId;
    @SerializedName(STATUS)
    private int status;

    public String getAlert() {
        return alert;
    }

    public void setAlert(final String alert) {
        this.alert = alert;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.alert);
        dest.writeInt(this.type);
        dest.writeInt(this.ticketId);
        dest.writeInt(this.status);
    }

    public PushInfo() {
    }

    protected PushInfo(Parcel in) {
        this.alert = in.readString();
        this.type = in.readInt();
        this.ticketId = in.readInt();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<PushInfo> CREATOR = new Parcelable.Creator<PushInfo>() {
        @Override
        public PushInfo createFromParcel(Parcel source) {
            return new PushInfo(source);
        }

        @Override
        public PushInfo[] newArray(int size) {
            return new PushInfo[size];
        }
    };
}
