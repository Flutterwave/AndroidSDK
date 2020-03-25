package com.flutterwave.raveandroid.data;

import android.util.Log;

import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.rave_remote.NetworkRequestImpl;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.EventBody;

import javax.inject.Inject;

public class EventLogger {
    @Inject
    NetworkRequestImpl networkRequest;

    @Inject
    public EventLogger() {
    }

    public void logEvent(final Event body, String publicKey) {
        EventBody eventBody = new EventBody(publicKey,
                body.getTitle(),
                body.getMessage());

        networkRequest.logEvent(eventBody, new ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Log.d("Event log successful", body.getTitle());
            }

            @Override
            public void onError(String message) {
                Log.d("Event log failed", body.getMessage() + message);
            }
        });
    }
}
