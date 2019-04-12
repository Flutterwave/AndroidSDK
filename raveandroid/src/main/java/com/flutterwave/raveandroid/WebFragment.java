package com.flutterwave.raveandroid;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {
    public static final String EXTRA_WEB = "extraWEB";
    public static final String EXTRA_AUTH_URL = "authUrl";
    public static final String EXTRA_FLW_REF = "flwref";
    private static final String LOG_TAG = "WebFragment";
    String authurl;
    String flwRef;
    WebView webView;
    ProgressDialog progessDialog;


    public WebFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web, container, false);
        webView = (WebView) v.findViewById(R.id.rave_webview);
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

    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
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
                // Get flutterwave ref
                Uri uri = Uri.parse(url);
                Set<String> args = uri.getQueryParameterNames();
                if (args.contains("response")) {
                    String response = uri.getQueryParameter("response");
                    try {
                        JsonElement respElement = new Gson().fromJson(response, JsonElement.class);
                        JsonObject responseJSONObject = respElement.getAsJsonObject();

                        flwRef = responseJSONObject.get("flwRef").getAsString();
                    } catch (com.google.gson.JsonSyntaxException | java.lang.IllegalStateException ex) {
                        Log.d(LOG_TAG, "Invalid JSON syntax for response returned");
                    }
                } else Log.d(LOG_TAG, "Callback does not contain \"response\" field");

                goBack();
            }
        }
    }

    public void goBack() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_FLW_REF, flwRef);
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

            if (progessDialog == null) {
                progessDialog = new ProgressDialog(getActivity());
                progessDialog.setCanceledOnTouchOutside(false);
                progessDialog.setMessage("Please wait...");
            }

            if (active && !progessDialog.isShowing()) {
                progessDialog.show();
            } else {
                progessDialog.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
