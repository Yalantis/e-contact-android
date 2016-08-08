package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class TypeSerializer implements JsonSerializer<ua.gov.dp.econtact.model.Type> {

    @Override
    public JsonElement serialize(final ua.gov.dp.econtact.model.Type src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ua.gov.dp.econtact.model.Type.ID, src.getId());
        jsonObject.addProperty(ua.gov.dp.econtact.model.Type.NAME, src.getTitle());
        return jsonObject;
    }
}


