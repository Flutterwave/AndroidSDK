package com.flutterwave.raveandroid.rave_presentation.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_ERROR;

public class ErrorEvent {
    Event event;

    public ErrorEvent(String errorMessage) {
        event = new Event(EVENT_TITLE_ERROR, "Error: " + errorMessage);
    }

    public Event getEvent() {
        return event;
    }
}
