package com.flutterwave.raveandroid;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {
    public static final String EXTRA_WEB = "extraWEB";
    public static final String EXTRA_AUTH_URL = "authUrl";
    String authurl;
    WebView webView;
    ProgressDialog progressDialog;


    public WebFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web, container, false);
        webView = v.findViewById(R.id.rave_webview);
        authurl = getArguments().getString(EXTRA_AUTH_URL);
        onDisplayInternetBankingPage(authurl);
        return v;
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

    public void goBack(){
        Intent intent = new Intent();
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
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
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
