package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.address.City;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class CitySerializer implements JsonSerializer<City> {

    @Override
    public JsonElement serialize(final City src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(City.ID, src.getId());
        jsonObject.addProperty(City.NAME, src.getTitle());
        return jsonObject;
    }
}


