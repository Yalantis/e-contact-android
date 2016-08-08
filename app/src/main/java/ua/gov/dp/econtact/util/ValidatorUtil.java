package ua.gov.dp.econtact.util;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by Ed
 */
public final class ValidatorUtil {

    private static final String PATTERN_EMAIL = "^([\\w]+)(([-\\.\\+][\\w]+)?)*@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    private static final String PATTERN_NAME = "^[а-яА-Яa-zA-Z-іІїЇґҐєЄ'`\\s]{2,32}$";
    private static final String PATTERN_FLAT_FIRST_TYPE = "^([\\d+]{1,5}[-][а-яА-Яa-zA-Z-іІїЇґҐєЄ'`\\s])$";
    private static final String PATTERN_FLAT_SECOND_TYPE = "^([\\d+]{1,5})$";
    private static final String PATTERN_FLAT_THIRD_TYPE = "^([\\d+]{1,5}[а-яА-Яa-zA-Z-іІїЇґҐєЄ'`\\s])$";
    private static final String PATTERN_FLAT_FOURTH_TYPE = "^([\\d+]{1,5}[-])$";

    private ValidatorUtil() {
    }

    public static boolean isEmail(final String email) {
        return email != null && Pattern.matches(PATTERN_EMAIL, email);
    }

    public static boolean isEmpty(final String... names) {
        if (names == null) {
            return true;
        }
        for (String name : names) {
            if (TextUtils.isEmpty(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidName(final String... names) {
        if (names == null) {
            return false;
        }
        for (String name : names) {
            if (TextUtils.isEmpty(name) || !Pattern.matches(PATTERN_NAME, name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidEmail(final String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static boolean isValidFlat(final String flat) {
        Pattern patternFirst = Pattern.compile(PATTERN_FLAT_FIRST_TYPE, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern patternSecond = Pattern.compile(PATTERN_FLAT_SECOND_TYPE, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern patternThird = Pattern.compile(PATTERN_FLAT_THIRD_TYPE, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern patternFourth = Pattern.compile(PATTERN_FLAT_FOURTH_TYPE, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        return (patternFirst.matcher(flat).matches()
                || patternSecond.matcher(flat).matches()
                || patternThird.matcher(flat).matches())
                && !patternFourth.matcher(flat).matches()
                || TextUtils.isEmpty(flat);
    }

    public static boolean isEmptyArray(final Object[] objects) {
        return objects == null || objects.length == 0;
    }
}
