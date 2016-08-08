package ua.gov.dp.econtact.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Yalantis
 * 24.12.2015.
 *
 * @author Pavel
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
