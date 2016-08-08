package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.address.CityDistrict;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class CityDistrictSerializer implements JsonSerializer<CityDistrict> {

    @Override
    public JsonElement serialize(final CityDistrict src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CityDistrict.ID, src.getId());
        jsonObject.addProperty(CityDistrict.NAME, src.getTitle());
        return jsonObject;
    }
}


