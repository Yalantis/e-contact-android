package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.address.District;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class DistrictSerializer implements JsonSerializer<District> {

    @Override
    public JsonElement serialize(final District src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(District.ID, src.getId());
        jsonObject.addProperty(District.NAME, src.getTitle());
        return jsonObject;
    }
}


