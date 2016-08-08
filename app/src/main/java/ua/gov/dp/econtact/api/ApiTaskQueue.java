package ua.gov.dp.econtact.api;

import ua.gov.dp.econtact.api.task.ApiTask;

import java.util.ArrayDeque;

/**
 * Created by: Dmitriy Dovbnya
 * Date: 08.10.13 18:04
 */
public class ApiTaskQueue extends ArrayDeque<ApiTask> {

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

}
