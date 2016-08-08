package ua.gov.dp.econtact.gallery.databinders;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class for holding position if selected views
 * while saving the mActivity state
 *
 * @author Ed Baev
 */
public class SavedIndexes implements Parcelable {

    private String position;

    public SavedIndexes() {
    }

    public SavedIndexes(final Parcel source) {
        readFromParcel(source);
    }

    private void readFromParcel(final Parcel source) {
        position = source.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(position);
    }


    /**
     * function for getting position
     *
     * @return position
     */
    public String getPosition() {
        return position;
    }


    /**
     * function for setting position
     *
     * @param positions
     */
    public void setPositions(final String positions) {
        this.position = positions;
    }

    public static final Creator<SavedIndexes> CREATOR = new Creator<SavedIndexes>() {

        @Override
        public SavedIndexes[] newArray(final int size) {
            return new SavedIndexes[size];
        }

        @Override
        public SavedIndexes createFromParcel(final Parcel source) {
            return new SavedIndexes(source);
        }
    };

}
