package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.Facilities;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class FacilitiesSerializer implements JsonSerializer<Facilities> {

    @Override
    public JsonElement serialize(final Facilities src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Facilities.ID, src.getId());
        jsonObject.addProperty(Facilities.NAME, src.getTitle());
        return jsonObject;
    }
}


