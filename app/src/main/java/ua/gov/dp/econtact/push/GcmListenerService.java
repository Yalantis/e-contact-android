package ua.gov.dp.econtact.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import timber.log.Timber;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.TicketActivity;
import ua.gov.dp.econtact.model.PushInfo;

/**
 * Created by cleanok on 18.05.16.
 */
public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification(data);
    }

    private void sendNotification(Bundle data) {
        try {
            PushInfo pushInfo = new PushInfo();
            pushInfo.setAlert(data.getString(PushInfo.ALERT));
            pushInfo.setType(data.getInt(PushInfo.TYPE));
            pushInfo.setTicketId(Integer.parseInt(data.getString(PushInfo.TICKET_ID)));
            pushInfo.setStatus(Integer.parseInt(data.getString(PushInfo.STATUS)));

            Intent intent = TicketActivity.newIntent(this, pushInfo.getTicketId(), true, false);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_push24)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_push64))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(pushInfo.getAlert())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(pushInfo.getTicketId(), notificationBuilder.build());
        } catch (NumberFormatException e) {
            Timber.e(new IllegalStateException(), "got this push data: %s", String.valueOf(data));
        }
    }
}
