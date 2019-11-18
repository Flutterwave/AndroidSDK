package com.flutterwave.raveandroid.data;

import android.util.Log;

import javax.inject.Inject;

public class EventLogger {
    @Inject
    NetworkRequestImpl networkRequest;

    @Inject
    public EventLogger() {
    }

    public void logEvent(final Event body, String publicKey) {
        EventBody eventBody = new EventBody(publicKey,
                body.title,
                body.message);

        networkRequest.logEvent(eventBody, new Callbacks.OnLogEventComplete() {
            @Override
            public void onSuccess(String response) {
                Log.d("Event log successful", body.title);
            }

            @Override
            public void onError(String message) {
                Log.d("Event log failed", body.title + message);
            }
        });
    }
}
