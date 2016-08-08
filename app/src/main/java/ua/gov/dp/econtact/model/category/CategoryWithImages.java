package ua.gov.dp.econtact.model.category;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cleanok on 24.05.16.
 */
public class CategoryWithImages extends RealmObject {

    public static final String ID = "id";
    @PrimaryKey
    @SerializedName(ID)
    private long id;
    @SerializedName("name")
    private String name;
    @Ignore
    @SerializedName("images")
    private Images mImages;
    private String smallImage;


    public CategoryWithImages(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryWithImages() {
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Images getImages() {
        return mImages;
    }

    public void setImages(Images images) {
        mImages = images;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getSmallImage() {
        return smallImage;
    }
}
