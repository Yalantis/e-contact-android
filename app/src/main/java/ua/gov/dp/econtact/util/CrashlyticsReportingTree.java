package ua.gov.dp.econtact.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;


/**
 * Created by Artem Kholodnyi on 11/4/15.
 */

public class CrashlyticsReportingTree extends Timber.Tree {
    private final static int MIN_LOG_PRIORITY = Log.DEBUG;
    private static final String DELIMITER = "::";

    @Override
    public void log(int priority, String tag, String message, Throwable t) {
        if (priority < MIN_LOG_PRIORITY) {
            return;
        }

        Crashlytics.log(tag + DELIMITER + message);

        if (t != null) {
            Crashlytics.logException(t);
        }
    }
}