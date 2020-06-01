package com.flutterwave.raveandroid.rave_logger.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_TYPING;

public class StartTypingEvent {
    Event event;

    public StartTypingEvent(String fieldName) {
        event = new Event(EVENT_TITLE_TYPING, "Started Entering " + fieldName);
    }

    public Event getEvent() {
        return event;
    }
}
