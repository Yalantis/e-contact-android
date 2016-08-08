package ua.gov.dp.econtact.event;

/**
 * Created by Yalantis
 * 25.09.2014.
 *
 * @author Dmitriy Dovbnya
 */
public class ApiEvent<T> implements BaseEvent {

    private final T mData;

    public ApiEvent(final T data) {
        mData = data;
    }

    public T getData() {
        return mData;
    }
}
