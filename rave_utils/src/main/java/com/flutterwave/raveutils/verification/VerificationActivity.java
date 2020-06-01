package com.flutterwave.raveutils.verification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.di.EventLoggerModule;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;
import com.flutterwave.raveutils.R;
import com.flutterwave.raveutils.di.DaggerVerificationComponent;
import com.flutterwave.raveutils.di.VerificationComponent;
import com.flutterwave.raveutils.verification.web.WebFragment;

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
    VerificationComponent verificationComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null & getIntent().getIntExtra("theme", 0) != 0) {
            setTheme(getIntent().getIntExtra("theme", 0));
        }
        setContentView(R.layout.rave_sdk_activity_futher_verification);
        buildGraph();
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
                    case RaveConstants.BARTER_CHECKOUT:
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

    public VerificationComponent getVerificationComponent() {
        return verificationComponent;
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

        verificationComponent = DaggerVerificationComponent.builder()
                .remoteModule(new RemoteModule(BASE_URL))
                .eventLoggerModule(new EventLoggerModule())
                .build();
    }
}
