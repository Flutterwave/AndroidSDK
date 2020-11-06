package com.flutterwave.raveutils.verification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.di.EventLoggerModule;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;
import com.flutterwave.raveutils.R;
import com.flutterwave.raveutils.di.DaggerVerificationComponent;
import com.flutterwave.raveutils.di.VerificationComponent;
import com.flutterwave.raveutils.verification.web.WebFragment;

import java.util.UUID;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.EMBED_FRAGMENT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_REQUEST_KEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.STAGING_URL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VERIFICATION_REQUEST_KEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VIEW_ID;

public class VerificationFragment extends Fragment {
    private static final String TAG = VerificationFragment.class.getName();
    public static final String ACTIVITY_MOTIVE = "activityMotive";
    public static final String PUBLIC_KEY_EXTRA = "publicKey";
    public static final String EXTRA_IS_STAGING = "isStaging";
    public static final String INTENT_SENDER = "sender";
    public static String BASE_URL;
    private Fragment fragment;
    static VerificationComponent verificationComponent;

    private int viewId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.rave_sdk_activity_futher_verification, container, false);
        
        if (getArguments() != null) {
            viewId = getArguments().getInt(VIEW_ID);
            if (getArguments().getInt("theme", 0) != 0){
                getActivity().setTheme(getArguments().getInt("theme", 0));
            }
        }

        buildGraph();

        if (rootView.findViewById(R.id.frame_container) != null) {
            if (savedInstanceState != null) {
                return rootView;
            }
            if (getArguments() != null & getArguments().getString(ACTIVITY_MOTIVE) != null) {
                switch (getArguments().getString(ACTIVITY_MOTIVE).toLowerCase()) {
                    case "otp":
                        fragment = new OTPFragment();
                        Bundle otpBundle = getArguments();
                        otpBundle.putBoolean(EMBED_FRAGMENT, true);
                        fragment.setArguments(otpBundle);

                        String otpTag = UUID.randomUUID().toString();
                        ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                                .add(viewId, fragment, otpTag).addToBackStack("").commit();

                        break;
                    case "pin":
                        fragment = new PinFragment();
                        Bundle pinBundle = getArguments();
                        pinBundle.putBoolean(EMBED_FRAGMENT, true);
                        fragment.setArguments(pinBundle);

                        String pinTag = UUID.randomUUID().toString();
                        ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                                .add(viewId, fragment, pinTag).addToBackStack("").commit();

                        break;
                    case "web":
                    case RaveConstants.BARTER_CHECKOUT:
                        fragment = new WebFragment();
                        Bundle webBundle = getArguments();
                        webBundle.putBoolean(EMBED_FRAGMENT, true);
                        fragment.setArguments(webBundle);

                        String webTag = UUID.randomUUID().toString();
                        ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                                .add(viewId, fragment, webTag).commit();

                        break;
                    case "avsvbv":
                        fragment = new AVSVBVFragment();
                        Bundle avsvbvBundle = getArguments();
                        avsvbvBundle.putBoolean(EMBED_FRAGMENT, true);
                        fragment.setArguments(avsvbvBundle);

                        String avsvbvTag = UUID.randomUUID().toString();
                        ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                                .add(viewId, fragment, avsvbvTag).commit();
                        break;
                    default:
                        Log.e(TAG, "No extra value matching motives");
                }
            }

//            getParentFragmentManager().setFragmentResultListener(VERIFICATION_REQUEST_KEY, this, new FragmentResultListener() {
//                @Override
//                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
////                    Fragment fragment = getParentFragmentManager().findFragmentById(viewId);
////                    fragment.getParentFragmentManager().setFragmentResult(requestKey, result);
////                    fragment.getParentFragmentManager().popBackStack();
//                }
//            });

        }

        return rootView;
    }

    public static VerificationComponent getVerificationComponent() {
        return verificationComponent;
    }

    private void buildGraph() {
        boolean isStaging = getArguments().getBoolean(EXTRA_IS_STAGING, false);

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
