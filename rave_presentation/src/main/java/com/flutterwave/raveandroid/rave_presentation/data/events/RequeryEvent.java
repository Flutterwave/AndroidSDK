package com.flutterwave.raveandroid.rave_presentation.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_REQUERY;

public class RequeryEvent {
    Event event;

    public RequeryEvent() {
        event = new Event(EVENT_TITLE_REQUERY, "Requerying transaction");
    }

    public Event getEvent() {
        return event;
    }
}
