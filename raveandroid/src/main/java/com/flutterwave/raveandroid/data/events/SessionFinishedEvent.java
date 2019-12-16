package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_FINISH;

public class SessionFinishedEvent {
    Event event;

    public SessionFinishedEvent(String message) {
        event = new Event(EVENT_TITLE_FINISH, message);
    }

    public Event getEvent() {
        return event;
    }
}
