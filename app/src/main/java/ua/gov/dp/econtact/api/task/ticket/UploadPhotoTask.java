package ua.gov.dp.econtact.api.task.ticket;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.api.task.ApiTask;
import ua.gov.dp.econtact.event.tickets.TicketImageErrorEvent;
import ua.gov.dp.econtact.event.tickets.TicketImageEvent;
import ua.gov.dp.econtact.model.Ticket;

import java.io.File;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import retrofit.client.Response;

/**
 * Create tickets
 */
public class UploadPhotoTask extends ApiTask<Void, Void> {
    private Context context;
    private String filePath;
    private long ticketId;

    public UploadPhotoTask(final Context context, final String filePath, final long ticketId) {
        super(null, null);
        this.context = context;
        this.filePath = filePath;
        this.ticketId = ticketId;
    }

    @Override
    public void run() {
        File file = new File(filePath);
        Ion.with(context)
                .load(ApiSettings.SERVER + ApiSettings.URL.TICKET + "/" + ticketId + "/file")
                .setLogging("MyLogs", Log.DEBUG)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(final long downloaded, final long total) {
                        Log.d("images", "" + downloaded + " / " + total);
                    }
                })
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + App.accountManager.getAuthToken())
                .setMultipartFile("ticket_image", "image/jpeg", file)
                .asString()/*as(TicketDto.class)*/.withResponse()
                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                    @Override
                    public void onCompleted(final Exception e, final com.koushikdutta.ion.Response<String> result) {
                        if (e != null) {
                            EventBus.getDefault().postSticky(new TicketImageErrorEvent());
                            return;
                        }
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.createOrUpdateObjectFromJson(Ticket.class, result.getResult());
                        realm.commitTransaction();
                        realm.close();
                        EventBus.getDefault().postSticky(new TicketImageEvent(ticketId));

                    }

                });

    }

    @Override
    public void onSuccess(final Void aVoid, final Response response) {
        finished();
    }
}
