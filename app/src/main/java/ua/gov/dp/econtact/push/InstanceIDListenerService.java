package ua.gov.dp.econtact.push;

import android.content.Intent;

/**
 * Created by cleanok on 18.05.16.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationService.class);
        startService(intent);
    }
}
