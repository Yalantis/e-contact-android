package ua.gov.dp.econtact.util;

import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Yalantis
 * 25.09.2014.
 *
 * @author Dmitriy Dovbnya
 */
public class CachedValue<T> {

    private static final Object lock = new Object();

    private static SharedPreferences sSharedPref;

    private SharedPreferences mSharedPrefs;

    private T mValue;
    private T mDefValue;
    private Class mType;
    private String mName;
    private boolean isLoaded = false;

    public CachedValue(final String name, final Class type) {
        this(name, null, null, type);
    }

    public CachedValue(final String name, final T defValue, final Class type) {
        this(name, null, defValue, type);
    }

    public CachedValue(final String name, final T value, final T defValue, final Class type) {
        mSharedPrefs = sSharedPref;
        mName = name;
        mType = type;
        isLoaded = value != null;
        mValue = value;
        mDefValue = defValue;
    }

    public void setValue(final T value) {
        synchronized (lock) {
            isLoaded = true;
            mValue = value;
            write(mValue);
        }
    }

    public T getValue() {
        synchronized (lock) {
            if (!isLoaded) {
                mValue = load();
                isLoaded = true;
            }
            return mValue;
        }
    }

    public String getName() {
        return mName;
    }

    private void write(final T value) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();

        if (value instanceof String) {

            editor.putString(mName, (String) value);

        } else if (value instanceof Integer) {

            editor.putInt(mName, (Integer) value);

        } else if (value instanceof Float) {

            editor.putFloat(mName, (Float) value);

        } else if (value instanceof Long) {

            editor.putLong(mName, (Long) value);

        } else if (value instanceof Boolean) {

            editor.putBoolean(mName, (Boolean) value);

        } else if (value instanceof Set) {

            editor.putStringSet(mName, (Set<String>) value);

        }

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    private T load() {

        if (mType == String.class) {

            return (T) mSharedPrefs.getString(mName, (String) mDefValue);

        } else if (mType == Integer.class) {

            return (T) Integer.valueOf(mSharedPrefs.getInt(mName, (Integer) mDefValue));

        } else if (mType == Float.class) {

            return (T) Float.valueOf(mSharedPrefs.getFloat(mName, (Float) mDefValue));

        } else if (mType == Long.class) {

            return (T) Long.valueOf(mSharedPrefs.getLong(mName, (Long) mDefValue));

        } else if (mType == Boolean.class) {

            return (T) Boolean.valueOf(mSharedPrefs.getBoolean(mName, (Boolean) mDefValue));

        } else if (mType == Set.class) {

            return (T) mSharedPrefs.getStringSet(mName, (Set<String>) mDefValue);

        }

        return null;
    }

    public void delete() {
        synchronized (lock) {
            mSharedPrefs.edit().remove(mName).apply();
            clear();
        }
    }

    public static void initialize(final SharedPreferences sp) {
        CachedValue.sSharedPref = sp;
    }

    public void setSharedPreferences(final SharedPreferences sp) {
        this.mSharedPrefs = sp;
    }

    public void clear() {
        synchronized (lock) {
            isLoaded = false;
            this.mValue = null;
        }
    }
}
