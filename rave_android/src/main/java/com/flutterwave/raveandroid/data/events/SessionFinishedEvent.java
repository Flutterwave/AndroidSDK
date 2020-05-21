package com.flutterwave.raveandroid.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_FINISH;

public class SessionFinishedEvent {
    Event event;

    public SessionFinishedEvent(String message) {
        event = new Event(EVENT_TITLE_FINISH, message);
    }

    public Event getEvent() {
        return event;
    }
}
