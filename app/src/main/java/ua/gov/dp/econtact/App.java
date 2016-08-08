package ua.gov.dp.econtact;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.multidex.MultiDexApplication;
import android.view.Display;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.Locale;

import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;
import ua.gov.dp.econtact.event.LogoutEvent;
import ua.gov.dp.econtact.manager.ApiManager;
import ua.gov.dp.econtact.manager.DataManager;
import ua.gov.dp.econtact.manager.EContactAccountManager;
import ua.gov.dp.econtact.manager.SharedPrefManager;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.util.CrashlyticsReportingTree;

/**
 * Created by Yalantis
 */
public class App extends MultiDexApplication {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "oYkR3JMKhfd982Ng6y4hKnxPU";
    private static final String TWITTER_SECRET = "qkaEYrKObuzVgUVOKAdBhb8EZ25PTRdBn5EffZvFDUKxvsbLvZ";

    private static Context context;
    private static int screenWidth;

    public static final EventBus eventBus = EventBus.getDefault();
    public static final ApiManager apiManager = new ApiManager();
    public static final DataManager dataManager = new DataManager();
    public static final SharedPrefManager spManager = new SharedPrefManager();
    public static final EContactAccountManager accountManager = new EContactAccountManager();

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits());
        FacebookSdk.sdkInitialize(getApplicationContext());

        setupRealmDefaultInstance();
        context = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(App.context, new Crashlytics());
            Timber.plant(new CrashlyticsReportingTree());
        }
        accountManager.init(this);
        dataManager.init(this);
        spManager.init(this);
        apiManager.init(this);
        App.apiManager.getCategoriesAll();
        setupScreenSize();
        Locale locale = new Locale("uk", "UA");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public static Context getContext() {
        return context;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public void clear() {
        apiManager.clear();
        dataManager.clear();
        spManager.clear();
    }

    private void setupRealmDefaultInstance() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .name(Const.Realm.STORAGE_DEFAULT).build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    private void setupScreenSize() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    public static void clearSession() {
        accountManager.purgeAccounts();
        dataManager.deleteTicketByState(TicketStates.DRAFT.getStates());
        dataManager.deleteTicketByState(TicketStates.MY_TICKET.getStates());
        dataManager.deleteUser();
        spManager.clearId();
        spManager.clearFbId();
        Digits.getSessionManager().clearActiveSession();
        eventBus.postSticky(new LogoutEvent());
    }
}
