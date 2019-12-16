package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_TYPING;

public class StartTypingEvent {
    Event event;

    public StartTypingEvent(String fieldName) {
        event = new Event(EVENT_TITLE_TYPING, "Started Entering " + fieldName);
    }

    public Event getEvent() {
        return event;
    }
}
