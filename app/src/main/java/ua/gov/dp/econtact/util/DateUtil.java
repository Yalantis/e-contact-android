package ua.gov.dp.econtact.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yalantis
 *
 * @author Oleksii Shliama
 */

public final class DateUtil {

    private static final String FULL_DATE_TIME_FORMAT = "d MMMM yyyy, hh:mm";
    private static final SimpleDateFormat mFullDateFormatter = new SimpleDateFormat(FULL_DATE_TIME_FORMAT, new Locale("uk", "UA"));

    private static final String DATE_FORMAT = "d MMMM yyyy";
    private static final SimpleDateFormat mDateFormatter = new SimpleDateFormat(DATE_FORMAT, new Locale("uk", "UA"));

    private static final String TIME_FORMAT = "hh:mm";
    private static final SimpleDateFormat mTimeFormatter = new SimpleDateFormat(TIME_FORMAT, new Locale("uk", "UA"));

    private static final String DUE_DATE_FORMAT = "MMM dd, yyyy";
    private static SimpleDateFormat mDueDateFormatter = new SimpleDateFormat(DUE_DATE_FORMAT, new Locale("uk", "UA"));

    private DateUtil() {

    }

    /**
     * Converts date in millis into (hh:mm).
     *
     * @return - formatted date string
     */
    public static String getFormattedTime(final long millisDate) {
        return mTimeFormatter.format(new Date(millisDate)).toLowerCase();
    }

    /**
     * Converts date in millis into (d MMMM yyyy).
     *
     * @return - formatted date string
     */
    public static String getFormattedDate(final long millisDate) {
        return mDateFormatter.format(new Date(millisDate));
    }

    /**
     * Converts date in millis into (d MMMM yyyy, hh:mm).
     *
     * @return - formatted date string
     */
    public static String getFullFormattedDate(final long millis) {
        return mFullDateFormatter.format(new Date(millis));
    }

    public static String getDueDateFormattedDate(final long millis) {
        String string = mDueDateFormatter.format(new Date(millis));
        String firstLetter = string.substring(0, 1);
        firstLetter = firstLetter.toUpperCase();
        return firstLetter + string.substring(1);
    }
}
