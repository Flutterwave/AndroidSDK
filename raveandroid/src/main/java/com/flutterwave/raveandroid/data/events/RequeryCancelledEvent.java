package com.flutterwave.raveandroid.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_REQUERY;

public class RequeryCancelledEvent {
    Event event;

    public RequeryCancelledEvent() {
        event = new Event(EVENT_TITLE_REQUERY, "Requery Cancelled");
    }

    public Event getEvent() {
        return event;
    }
}
