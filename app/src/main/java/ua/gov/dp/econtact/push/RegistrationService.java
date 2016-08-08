package ua.gov.dp.econtact.push;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import timber.log.Timber;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.api.task.UpdateTokenTask;

/**
 * Created by cleanok on 18.05.16.
 */
public class RegistrationService extends IntentService {

    private String mToken;
    private int mPushCallCount;
    private final static int MAX_PUSH_CALLS = 3;
    private UpdateTokenTask.Callback mCallback = new UpdateTokenTask.Callback() {
        @Override
        public void onSuccess() {
            mPushCallCount = 0;
        }

        @Override
        public void onFailure(int statusCode) {
            // Need to retry api call up to 3 times in cases of server error or connection failure
            // statusCode=400 is case when there is problem with request itself
            if (mPushCallCount < MAX_PUSH_CALLS && statusCode != Const.ApiCode.CODE_BAD_REQUEST) {
                sendRegistrationToServer();
            } else {
                mPushCallCount = 0;
            }
        }
    };

    public RegistrationService() {
        super(RegistrationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            InstanceID instanceID = InstanceID.getInstance(this);

            mToken = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer();
            App.spManager.setTokenSent(true);
        } catch (Exception e) {
            Timber.e(e, "Failed to complete token refresh");
            App.spManager.setTokenSent(false);
        }
    }

    private void sendRegistrationToServer() {
        if (!TextUtils.isEmpty(App.accountManager.getAuthToken())) {
            mPushCallCount++;
            App.apiManager.updateToken(mToken, mCallback);
        }
    }
}
