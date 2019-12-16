package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_REQUERY;

public class RequeryEvent {
    Event event;

    public RequeryEvent() {
        event = new Event(EVENT_TITLE_REQUERY, "Requerying transaction");
    }

    public Event getEvent() {
        return event;
    }
}
