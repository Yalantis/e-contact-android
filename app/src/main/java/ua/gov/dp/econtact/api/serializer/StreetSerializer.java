package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.address.Street;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class StreetSerializer implements JsonSerializer<Street> {

    @Override
    public JsonElement serialize(final Street src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Street.ID, src.getId());
        jsonObject.addProperty(Street.NAME, src.getName());
        jsonObject.addProperty(Street.NAME_RU, src.getNameRu());
        return jsonObject;
    }
}


