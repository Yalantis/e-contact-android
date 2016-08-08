package ua.gov.dp.econtact.api;

import ua.gov.dp.econtact.api.task.ApiTask;

/**
 * Created by: Dmitriy Dovbnya
 * Date: 21.09.13 17:05
 */
public class ApiTaskExecutor implements QueuedExecutorCallback {

    private ApiTaskQueue taskQueue = new ApiTaskQueue();

    public synchronized void execute(final ApiTask task) {
        task.setCallback(this);
        taskQueue.add(task);
        nextTask();
    }

    private synchronized void nextTask() {
        if (!taskQueue.isActive()) {
            ApiTask task = taskQueue.poll();
            if (task != null) {
                taskQueue.activate();
                new Thread(task).start();
            }
        }
    }

    @Override
    public void finished() {
        taskQueue.deactivate();
        nextTask();
    }

    public void clear() {
        taskQueue.clear();
    }

}
