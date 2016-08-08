package ua.gov.dp.econtact.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ua.gov.dp.econtact.model.address.Address;

import java.lang.reflect.Type;

/**
 * Created by Yalantis
 *
 * @author Ed Baev
 */
public class AddressSerializer implements JsonSerializer<Address> {

    @Override
    public JsonElement serialize(final Address src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Address.ID, src.getId());
        jsonObject.add(Address.CITY, context.serialize(src.getCity()));
        jsonObject.addProperty(Address.FLAT, src.getFlat());
        jsonObject.add(Address.HOUSE, context.serialize(src.getHouse()));
        jsonObject.add(Address.DISTRICT, context.serialize(src.getDistrict()));
        jsonObject.add(Address.STREET, context.serialize(src.getStreet()));
        return jsonObject;
    }
}


