package ua.gov.dp.econtact.api;

/**
 * Created by: Dmitriy Dovbnya
 * Date: 21.09.13 19:34
 */
public interface QueuedExecutorCallback {

    /**
     * Method that will be called when task is finished
     */
    void finished();

}
