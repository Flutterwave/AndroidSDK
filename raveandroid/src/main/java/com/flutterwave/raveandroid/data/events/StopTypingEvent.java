package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_TYPING;

public class StopTypingEvent {
    Event event;

    public StopTypingEvent(String fieldName) {
        event = new Event(EVENT_TITLE_TYPING, "Stopped Entering " + fieldName);
    }

    public Event getEvent() {
        return event;
    }
}
