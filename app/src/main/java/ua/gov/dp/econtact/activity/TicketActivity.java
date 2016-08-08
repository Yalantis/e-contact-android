package ua.gov.dp.econtact.activity;


import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.io.File;
import java.net.URI;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.auth.LoginActivity;
import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.api.task.ticket.LikeTask;
import ua.gov.dp.econtact.event.ErrorApiEvent;
import ua.gov.dp.econtact.event.GotTicketEvent;
import ua.gov.dp.econtact.event.tickets.TicketLikedEvent;
import ua.gov.dp.econtact.fragment.ticket.TicketOnMapFragment;
import ua.gov.dp.econtact.fragment.ticket.TicketsAnswerFragment;
import ua.gov.dp.econtact.fragment.ticket.TicketsDetailsFragment;
import ua.gov.dp.econtact.interfaces.TicketAnswerListener;
import ua.gov.dp.econtact.interfaces.TicketDetailsListener;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.dto.ErrorResponse;
import ua.gov.dp.econtact.util.MimeUtils;
import ua.gov.dp.econtact.util.Toaster;

public class TicketActivity extends BaseActivity implements TicketDetailsListener, TicketAnswerListener {

    public static final String ID = "id";
    public static final String IS_FROM_PUSH_KEY = "push_key";
    public static final String IS_FROM_MAP_KEY = "map_key";

    private Ticket mTicket;
    private CallbackManager mCallbackManager;
    private DownloadManager mDownloadManager;
    private long mDownloadId;
    private BroadcastReceiver mDownloadReceiver;

    public static Intent newIntent(Context context, long id, boolean isFromPush, boolean isFromMap) {
        Intent intent = new Intent(context, TicketActivity.class);
        Bundle data = new Bundle();
        data.putLong(TicketActivity.ID, id);
        data.putBoolean(TicketActivity.IS_FROM_PUSH_KEY, isFromPush);
        data.putBoolean(TicketActivity.IS_FROM_MAP_KEY, isFromMap);
        intent.putExtras(data);
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        showBackButton();
        showProgress();

        long ticketId = getIntent().getExtras().getLong(ID);
        User user = App.dataManager.getCurrentUser();
        if (user != null && user.getId() > 0 && !TextUtils.isEmpty(App.accountManager.getAuthToken())) {
            App.apiManager.getMyTicketById(ticketId);
        } else {
            App.apiManager.getTicketById(ticketId);
        }
        mCallbackManager = CallbackManager.Factory.create();


        mDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Cursor downloadedFileCursor = getCursorFromDownloadId(downloadId);
                    if (downloadedFileCursor.moveToFirst()) {
                        int columnIndex = downloadedFileCursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == downloadedFileCursor.getInt(columnIndex)) {
                            successDownload(downloadedFileCursor);
                        } else if (DownloadManager.STATUS_FAILED == downloadedFileCursor.getInt(columnIndex)) {
                            failedDownload();
                        }
                    }
                }
            }
        };

        registerReceiver(mDownloadReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void failedDownload() {
        hideProgress();
        Toaster.share(mToolbar, R.string.error_download_failed);
    }

    private void successDownload(Cursor downloadedFileCursor) {
        String stringUri = downloadedFileCursor.getString(downloadedFileCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        openFile(new File(URI.create(stringUri)));
        hideProgress();
    }

    private Cursor getCursorFromDownloadId(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        return mDownloadManager.query(query);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ticket, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().getBooleanExtra(IS_FROM_PUSH_KEY, false)) {
                    Intent intent = MainActivity.newInstance(TicketActivity.this, false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    super.onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showTicketOnMap() {
        replaceFragment(TicketOnMapFragment.newInstance(), true, R.id.wrapper_fragment);
    }

    @Override
    public void openTicketAnswer() {
        replaceFragment(TicketsAnswerFragment.newInstance(mTicket), true, R.id.wrapper_fragment);
    }

    @Override
    public void startFbLogin() {
        if (TextUtils.isEmpty(App.accountManager.getAuthToken())) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            loginFb();
        }
    }

    private void loginFb() {
        if (TextUtils.isEmpty(App.spManager.getFbUserId())) {
            LoginManager.getInstance().registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {
                            App.spManager.setFbUserId(loginResult.getAccessToken().getUserId());
                            sendLikeRequest();
                        }

                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onError(final FacebookException exception) {
                            Toast.makeText(TicketActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            LoginManager.getInstance().logInWithReadPermissions(this, Const.FB_PERMISSION);
        } else {
            sendLikeRequest();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadReceiver != null) {
            unregisterReceiver(mDownloadReceiver);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onEvent(final TicketLikedEvent event) {
        removeStickyEvent(event);
        TicketsDetailsFragment fragment = getDetailsFragment();
        if (fragment != null && fragment.isAdded()) {
            fragment.onFbLiked(event.getData().getLikesCount());
        }
    }

    public void onEvent(final ErrorApiEvent event) {
        removeStickyEvent(event);
        hideProgress();
        ErrorResponse errResponse = event.getErrorResponse();
        int error = errResponse != null && errResponse.getCode() == LikeTask.STATUS_LIKED
                ? R.string.liked_text : R.string.error;
        Toaster.share(mToolbar, error);
    }

    public void onEvent(GotTicketEvent ticketEvent) {
        hideProgress();
        removeStickyEvent(ticketEvent);
        mTicket = ticketEvent.getData();
        //Saving ticket to cache instead of DB
        App.dataManager.setCurrentTicket(ticketEvent.getData());
        replaceFragment(TicketsDetailsFragment.newInstance(), false, R.id.wrapper_fragment);
    }

    private void sendLikeRequest() {
        App.apiManager.likeTicket(mTicket.getId(), App.spManager.getFbUserId());
    }

    private TicketsDetailsFragment getDetailsFragment() {
        return (TicketsDetailsFragment) getSupportFragmentManager().findFragmentByTag(
                TicketsDetailsFragment.class.getSimpleName());
    }

    @Override
    public void onStartDownload(final String fileName) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ApiSettings.ANSWER_FILE + fileName));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            mDownloadId = mDownloadManager.enqueue(request);
            showProgress();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }

    private void openFile(final File file) {
        if (file == null) {
            Toaster.share(mToolbar, R.string.error_download_file);
        } else {
            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setDataAndType(Uri.fromFile(file), MimeUtils.getMimeType(file));
            Intent intent = Intent.createChooser(myIntent, getString(R.string.open_with));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toaster.share(mToolbar, getString(R.string.error_open_file) + " " + file.getPath());
            }
        }
    }

}
