package com.flutterwave.raveandroid.rave_presentation;

import android.content.Context;

import com.flutterwave.raveandroid.rave_presentation.account.AccountContract;
import com.flutterwave.raveandroid.rave_presentation.account.AccountPresenter;
import com.flutterwave.raveandroid.rave_presentation.di.AndroidModule;
import com.flutterwave.raveandroid.rave_presentation.di.DaggerRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.STAGING_URL;

public class RavePayManager {

    protected boolean staging = true;
    private Context context;


    public RavePayManager(Context context) {
        this.context = context;
    }

    /**
     * Sets up the SDK to accept payments
     *
     * @return Rave component to be used to create Rave presenters
     */
    public RaveComponent initializeRave() {
        return setUpGraph();
    }


    /**
     * Sets up the SDK to accept account payments
     *
     * @param view View object with overridden methods to handle responses
     * @return A presenter to handle the payment flow
     */
    public AccountPresenter setupAccountPayment(AccountContract.View view) {
        return new AccountPresenter(view, setUpGraph());
    }

    public RavePayManager onStagingEnv(boolean staging) {
        this.staging = staging;
        return this;
    }

    private RaveComponent setUpGraph() {
        String baseUrl;

        if (staging) {
            baseUrl = STAGING_URL;
        } else {
            baseUrl = LIVE_URL;
        }
        if (context != null) {
            return DaggerRaveComponent.builder()
                    .androidModule(new AndroidModule(context))
                    .remoteModule(new RemoteModule(baseUrl))
                    .build();
        } else throw new IllegalArgumentException("Context is required");
    }
}
