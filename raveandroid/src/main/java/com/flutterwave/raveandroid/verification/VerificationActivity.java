package com.flutterwave.raveandroid.verification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.di.components.AppComponent;
import com.flutterwave.raveandroid.di.components.DaggerAppComponent;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.EventLoggerModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.verification.web.WebFragment;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.STAGING_URL;

public class VerificationActivity extends AppCompatActivity {
    private static final String TAG = VerificationActivity.class.getName();
    public static final String ACTIVITY_MOTIVE = "activityMotive";
    public static final String PUBLIC_KEY_EXTRA = "publicKey";
    public static final String EXTRA_IS_STAGING = "isStaging";
    public static final String INTENT_SENDER = "sender";
    public static String BASE_URL;
    private Fragment fragment;
    AppComponent appComponent;

    RavePayInitializer ravePayInitializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rave_sdk_activity_futher_verification);
        buildGraph();
        if (getIntent() != null & getIntent().getIntExtra("theme", 0) != 0) {
            setTheme(getIntent().getIntExtra("theme", 0));
        }
        if (findViewById(R.id.frame_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            if (getIntent() != null & getIntent().getStringExtra(ACTIVITY_MOTIVE) != null) {
                switch (getIntent().getStringExtra(ACTIVITY_MOTIVE).toLowerCase()) {
                    case "otp":
                        fragment = new OTPFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    case "pin":
                        fragment = new PinFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    case "web":
                    case BARTER_CHECKOUT:
                        fragment = new WebFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    case "avsvbv":
                        fragment = new AVSVBVFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    default:
                        Log.e(TAG, "No extra value matching motives");
                }
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_container, fragment).commit();
            } else {
                finish();
            }
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void buildGraph() {
        boolean isStaging = getIntent().getBooleanExtra(EXTRA_IS_STAGING, false);

        if (isStaging) {
            BASE_URL = STAGING_URL;
        } else {
            BASE_URL = LIVE_URL;
        }


        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .networkModule(new NetworkModule(BASE_URL))
                .eventLoggerModule(new EventLoggerModule())
                .build();
    }
}
