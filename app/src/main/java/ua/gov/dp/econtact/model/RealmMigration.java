package ua.gov.dp.econtact.model;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;
import ua.gov.dp.econtact.Const;

/**
 * Created by Alexey on 25.07.2016.
 */
public class RealmMigration implements io.realm.RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.get("Ticket").removeField(Const.FIELD_CATEGORY).addRealmObjectField(Const.FIELD_CATEGORY, schema.get("Category"));
        }
        //TODO Write migration logic when realm models will change
    }
}
