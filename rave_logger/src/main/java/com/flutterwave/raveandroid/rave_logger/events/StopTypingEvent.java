package com.flutterwave.raveandroid.rave_logger.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_TYPING;

public class StopTypingEvent {
    Event event;

    public StopTypingEvent(String fieldName) {
        event = new Event(EVENT_TITLE_TYPING, "Stopped Entering " + fieldName);
    }

    public Event getEvent() {
        return event;
    }
}
