package ua.gov.dp.econtact.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

public class EContactAuthenticatorService extends Service {
    private EContactAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new EContactAuthenticator(this);
    }

    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
