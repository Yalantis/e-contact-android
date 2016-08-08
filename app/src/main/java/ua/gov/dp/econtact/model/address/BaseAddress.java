package ua.gov.dp.econtact.model.address;

/**
 * Created by Yalantis
 * 8/11/15.
 *
 * @author Ed Baev
 */
public class BaseAddress implements Comparable<BaseAddress> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String NAME_RU = "ru_name";

    private long id;
    private String title;
    private String nameRu;

    public BaseAddress(final long id, final String title, final String nameRu) {
        this.id = id;
        this.title = title;
        this.nameRu = nameRu;
    }

    public long getId() {
        return id;
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

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(final String nameRu) {
        this.nameRu = nameRu;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(final BaseAddress another) {
        return getTitle().compareTo(another.getTitle());
    }
}
