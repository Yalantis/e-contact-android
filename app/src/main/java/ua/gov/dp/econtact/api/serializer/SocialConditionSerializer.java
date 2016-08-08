package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.SocialCondition;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class SocialConditionSerializer implements JsonSerializer<SocialCondition> {

    @Override
    public JsonElement serialize(final SocialCondition src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SocialCondition.ID, src.getId());
        jsonObject.addProperty(SocialCondition.NAME, src.getTitle());
        return jsonObject;
    }
}


