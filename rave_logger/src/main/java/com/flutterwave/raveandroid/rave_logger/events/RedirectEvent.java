package com.flutterwave.raveandroid.rave_logger.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_REDIRECT;

public class RedirectEvent {
    Event event;

    public RedirectEvent(String url) {
        event = new Event(EVENT_TITLE_REDIRECT, "Redirecting to " + url);
    }

    public Event getEvent() {
        return event;
    }
}
