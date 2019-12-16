package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_REDIRECT;

public class RedirectEvent {
    Event event;

    public RedirectEvent(String url) {
        event = new Event(EVENT_TITLE_REDIRECT, "Redirecting to " + url);
    }

    public Event getEvent() {
        return event;
    }
}
