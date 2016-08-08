package ua.gov.dp.econtact.gallery.databinders;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * VO class for holding thumbnail data
 *
 * @author Ed Baev
 */
public class DataHolder implements Parcelable {

    private int clientId;

    private String mediaPath;
    private String thumbnailData;

    private String dataTaken;

    private int dataType;

    private String cropPath;
    private String uploadPath;
    private String uploadPathThump;
    private long id;

    public DataHolder() {
    }

    protected DataHolder(final Parcel in) {
        clientId = in.readInt();
        mediaPath = in.readString();
        thumbnailData = in.readString();
        dataTaken = in.readString();
        dataType = in.readInt();
        cropPath = in.readString();
        uploadPath = in.readString();
        uploadPathThump = in.readString();
        id = in.readLong();
    }

    @Override
    public DataHolder clone() {
        DataHolder dataHolder = new DataHolder();
        dataHolder.setCropPath(cropPath);
        dataHolder.setMediaPath(mediaPath);
        dataHolder.setDataType(dataType);
        dataHolder.setDataTaken(dataTaken);
        dataHolder.setThumbnailData(thumbnailData);
        dataHolder.setUploadPath(uploadPath);
        dataHolder.setId(id);
        dataHolder.setUploadPathThump(uploadPathThump);
        dataHolder.setClientId(clientId);
        return dataHolder;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(final int clientId) {
        this.clientId = clientId;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(final String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(final String thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public String getDataTaken() {
        return dataTaken;
    }

    public void setDataTaken(final String dataTaken) {
        this.dataTaken = dataTaken;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(final int dataType) {
        this.dataType = dataType;
    }

    public String getCropPath() {
        if (cropPath == null) {
            return mediaPath;
        }
        return cropPath;
    }

    public void setCropPath(final String cropPath) {
        this.cropPath = cropPath;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(final String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }


    @Override
    public boolean equals(final Object v) {
        boolean retVal = false;

        if (v instanceof DataHolder) {
            DataHolder ptr = (DataHolder) v;
            retVal = ptr.getMediaPath().equals(this.mediaPath);
        }

        return retVal;
    }


    public String getUploadPathThump() {
        return uploadPathThump;
    }

    public void setUploadPathThump(final String uploadPathThump) {
        this.uploadPathThump = uploadPathThump;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(clientId);
        dest.writeString(mediaPath);
        dest.writeString(thumbnailData);
        dest.writeString(dataTaken);
        dest.writeInt(dataType);
        dest.writeString(cropPath);
        dest.writeString(uploadPath);
        dest.writeString(uploadPathThump);
        dest.writeLong(id);
    }

    public static final Creator<DataHolder> CREATOR = new Creator<DataHolder>() {
        @Override
        public DataHolder createFromParcel(final Parcel in) {
            return new DataHolder(in);
        }

        @Override
        public DataHolder[] newArray(final int size) {
            return new DataHolder[size];
        }
    };
}
