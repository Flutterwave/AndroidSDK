package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_ERROR;

public class ErrorEvent {
    Event event;

    public ErrorEvent(String errorMessage) {
        event = new Event(EVENT_TITLE_ERROR, "Error: " + errorMessage);
    }

    public Event getEvent() {
        return event;
    }
}
