package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.User;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class UserSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(final User src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(User.ID, src.getId());
        jsonObject.addProperty(User.BIRTHDAY, src.getBirthdaySeconds());
        jsonObject.addProperty(User.EMAIL, src.getEmail());
        jsonObject.addProperty(User.FIRST_NAME, src.getFirstName());
        jsonObject.addProperty(User.IMAGE, src.getImage());
        jsonObject.addProperty(User.LAST_NAME, src.getLastName());
        jsonObject.addProperty(User.MIDDLE_NAME, src.getMiddleName());
        jsonObject.addProperty(User.PHONE, src.getPhone());

        jsonObject.add(User.ADDRESS, context.serialize(src.getAddress()));
        jsonObject.add(User.FACILITIES, context.serialize(src.getFacilities()));
        jsonObject.add(User.SOCIAL_CONDITION, context.serialize(src.getSocialCondition()));

        return jsonObject;
    }
}


