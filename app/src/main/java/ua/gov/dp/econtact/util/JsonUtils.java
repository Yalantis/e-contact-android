package ua.gov.dp.econtact.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import timber.log.Timber;

/**
 * Created by Yalantis
 * 13.10.2015.
 *
 * @author Aleksandr
 */
public final class JsonUtils {

    private static final String ATTRIBUTES = "attributes";
    private static final String EMAIL = "email";
    private static final String MESSAGE = "mMessage";
    private static final String ALREADY_USED = "already used";

    private JsonUtils() {

    }

    public static boolean isEmailError(final String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has(ATTRIBUTES)) {
                Iterator<String> keys = obj.getJSONObject(ATTRIBUTES).keys();
                while (keys.hasNext()) {
                    String attr = keys.next();
                    if (attr.equals(EMAIL)) {
                        JSONArray attrs = obj.getJSONObject(ATTRIBUTES).getJSONArray(attr);
                        for (int i = 0; i < attrs.length(); i++) {
                            JSONObject nextAttr = attrs.getJSONObject(i);
                            if (nextAttr.has(MESSAGE) && nextAttr.getString(MESSAGE).contains(ALREADY_USED)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, e.getMessage());
        }
        return false;
    }
}
