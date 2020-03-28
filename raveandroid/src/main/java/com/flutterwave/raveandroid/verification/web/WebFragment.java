package com.flutterwave.raveandroid.verification.web;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.di.modules.WebModule;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_presentation.RavePayInitializer;
import com.flutterwave.raveandroid.verification.VerificationActivity;

import javax.inject.Inject;

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
    RavePayInitializer ravePayInitializer;
    WebView webView;
    ProgressDialog progressDialog;
    @Inject
    WebPresenter presenter;


    public WebFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        injectComponents();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.rave_sdk_fragment_web, container, false);
        webView = v.findViewById(R.id.rave_webview);
        authurl = getArguments().getString(EXTRA_AUTH_URL);
        try {
            flwRef = getArguments().getString(EXTRA_FLW_REF);
            publicKey = getArguments().getString(EXTRA_PUBLIC_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onDisplayInternetBankingPage(authurl);

        initPresenter();
        return v;
    }

    private void initPresenter() {
        if (flwRef != null && publicKey != null)
            if (!flwRef.isEmpty() && !publicKey.isEmpty())
                presenter.init(flwRef, publicKey);
    }

    private void injectComponents() {
        if (getActivity() != null) {
            ((VerificationActivity) getActivity()).getRaveUiComponent()
                    .plus(new WebModule(this))
                    .inject(this);
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

    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(RaveConstants.RAVE_3DS_CALLBACK)) {
                hideWebview();
            }
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            if (request.getUrl().toString().contains(RaveConstants.RAVE_3DS_CALLBACK)) {
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
            if (url.contains(RaveConstants.RAVE_3DS_CALLBACK)) {
                goBack();
            }
        }
    }

    public void goBack() {
        Intent intent = new Intent();
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    private void goBack(int result, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra(RaveConstants.response, responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(result, intent);
            getActivity().finish();
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
    public void onPaymentSuccessful(String responseAsString) {
        goBack(RavePayActivity.RESULT_SUCCESS, responseAsString);
    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {
        goBack(RavePayActivity.RESULT_ERROR, responseAsJSONString);
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
