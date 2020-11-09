package com.flutterwave.raveutils.verification.web;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.RedirectEvent;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_logger.events.ScreenMinimizeEvent;
import com.flutterwave.raveutils.R;
import com.flutterwave.raveutils.di.WebModule;
import com.flutterwave.raveutils.verification.Utils;
import com.flutterwave.raveutils.verification.VerificationActivity;
import com.flutterwave.raveutils.verification.VerificationFragment;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.EMBED_FRAGMENT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RESULT_ERROR;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RESULT_SUCCESS;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VERIFICATION_REQUEST_KEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.WEB_VERIFICATION_REQUEST_CODE;
import static com.flutterwave.raveutils.verification.Utils.REQUEST_CODE;
import static com.flutterwave.raveutils.verification.Utils.RESULT_CODE;
import static com.flutterwave.raveutils.verification.VerificationActivity.PUBLIC_KEY_EXTRA;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment implements WebContract.View {
    public static final String EXTRA_WEB = "extraWEB";
    public static final String EXTRA_AUTH_URL = "authUrl";
    public static final String EXTRA_FLW_REF = "flwref";
    public static final String EXTRA_PUBLIC_KEY = "publicKey";
    String authurl;
    String flwRef = "";
    String publicKey = "";
    WebView webView;
    ProgressDialog progressDialog;
    @Inject
    WebPresenter presenter;
    @Inject
    EventLogger logger;


    public WebFragment() {
        // Required empty public constructor
    }

    private boolean embedFragment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.rave_sdk_fragment_web, container, false);


        if (getArguments() != null) {
            embedFragment = getArguments().getBoolean(EMBED_FRAGMENT);
        }

        injectComponents(embedFragment);

        webView = v.findViewById(R.id.rave_webview);
        authurl = getArguments().getString(EXTRA_AUTH_URL);
        try {
            flwRef = getArguments().getString(EXTRA_FLW_REF);
            publicKey = getArguments().getString(EXTRA_PUBLIC_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        onDisplayInternetBankingPage(authurl);

        Utils.onBackPressed(embedFragment, this, (AppCompatActivity) getActivity());

        logEvent(new ScreenLaunchEvent("Web Fragment").getEvent());
        initPresenter();
        return v;
    }

    private void initPresenter() {
        if (flwRef != null && publicKey != null)
            if (!flwRef.isEmpty() && !publicKey.isEmpty())
                presenter.init(flwRef, publicKey);
    }

    private void logEvent(Event event) {
        if (getArguments() != null
                & getArguments().getString(PUBLIC_KEY_EXTRA) != null
                & logger != null) {
            String publicKey = getArguments().getString(PUBLIC_KEY_EXTRA);
            event.setPublicKey(publicKey);
            logger.logEvent(event);
        }
    }

    private void injectComponents(boolean embedFragment) {
        if (getActivity() != null) {
            if (embedFragment) {
                VerificationFragment.getVerificationComponent()
                        .plus(new WebModule(this))
                        .inject(this);
            } else {
                ((VerificationActivity) getActivity()).getVerificationComponent()
                        .plus(new WebModule(this))
                        .inject(this);
            }
        }
    }

    public void onDisplayInternetBankingPage(String authurl) {

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        webView.setWebViewClient(new MyBrowser());
        // Load the initial URL
        webView.loadUrl(authurl);
    }

    private void hideWebview() {
        webView.setVisibility(View.INVISIBLE);
    }

    public void goBack() {

        if (embedFragment) {
            Bundle bundle = new Bundle();
            logEvent(new ScreenMinimizeEvent("Web Fragment").getEvent());

            bundle.putInt(RESULT_CODE, RESULT_SUCCESS);
            bundle.putInt(REQUEST_CODE, WEB_VERIFICATION_REQUEST_CODE);
            getParentFragmentManager().setFragmentResult(VERIFICATION_REQUEST_KEY, bundle);
            getParentFragmentManager().popBackStack();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        } else {
            Intent intent = new Intent();
            logEvent(new ScreenMinimizeEvent("Web Fragment").getEvent());
            if (getActivity() != null) {
                getActivity().setResult(RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        }

    }

    @Override
    public void onPaymentSuccessful(String responseAsString) {
        goBack(RESULT_SUCCESS, responseAsString);
    }

    private void goBack(int result, String responseAsJSONString) {

        if (embedFragment) {
            Bundle bundle = new Bundle();
            logEvent(new ScreenMinimizeEvent("Web Fragment").getEvent());

            bundle.putString(RaveConstants.response, responseAsJSONString);
            getParentFragmentManager().setFragmentResult(VERIFICATION_REQUEST_KEY, bundle);
            getParentFragmentManager().popBackStack();
        }else {

            Intent intent = new Intent();
            intent.putExtra(RaveConstants.response, responseAsJSONString);
            if (getActivity() != null) {
                getActivity().setResult(result, intent);
                getActivity().finish();
            }

        }

    }


    public void showProgressIndicator(boolean active) {

        try {
            if (getActivity().isFinishing()) {
                return;
            }

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");
            }

            if (active && !progressDialog.isShowing()) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {
        goBack(RESULT_ERROR, responseAsJSONString);
    }

    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            logEvent(new RedirectEvent(url).getEvent());
            if (url.contains(RaveConstants.RAVE_3DS_CALLBACK) || url.contains("http://127.0.0.0")) {
                hideWebview();
            }
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            logEvent(new RedirectEvent(request.getUrl().toString()).getEvent());
            if (request.getUrl().toString().contains(RaveConstants.RAVE_3DS_CALLBACK) || request.getUrl().toString().contains("http://127.0.0.0")) {
                hideWebview();
            }
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("started URLS", url);
            showProgressIndicator(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

//            Log.d("URLS", url);
            showProgressIndicator(false);

            Log.d("finished URLS", url);
            if (url.contains(RaveConstants.RAVE_3DS_CALLBACK) || url.contains("http://127.0.0.0")) {
                goBack();
            }
        }
    }

    @Override
    public void onPollingRoundComplete(String flwRef, String publicKey) {
        presenter.requeryTx(flwRef, publicKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onAttachView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.onDetachView();
        }
    }
}
