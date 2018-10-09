package com.flutterwave.raveandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.flutterwave.raveandroid.otp_pin_avsvbv_webview.AVSVBVFragment;
import com.flutterwave.raveandroid.otp_pin_avsvbv_webview.OTPFragment;
import com.flutterwave.raveandroid.otp_pin_avsvbv_webview.PinFragment;
import com.flutterwave.raveandroid.otp_pin_avsvbv_webview.WebFragment;

public class FutherVerificationActivity extends AppCompatActivity {
    private static final String TAG = FutherVerificationActivity.class.getName();
    public static final String ACTIVITY_MOTIVE = "activityMotive";
    public static final String INTENT_SENDER = "sender";
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_futher_verification);
        if (findViewById(R.id.frame_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            if(getIntent()!=null & getIntent().getStringExtra(ACTIVITY_MOTIVE)!=null){
                switch (getIntent().getStringExtra(ACTIVITY_MOTIVE).toLowerCase()){
                    case "otp":
                        fragment = new OTPFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    case "pin":
                        fragment = new PinFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    case "web":
                        fragment = new WebFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    case "avsvbv":
                        fragment = new AVSVBVFragment();
                        fragment.setArguments(getIntent().getExtras());
                        break;
                    default:
                        Log.e(TAG,"No extra value matching motives");
                }
            }

            if(fragment!=null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_container, fragment).commit();
            }else{
                Utils.toast(this,"No extra value matching motives");
                finish();
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
