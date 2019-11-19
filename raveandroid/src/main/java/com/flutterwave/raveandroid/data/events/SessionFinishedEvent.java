package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_LAUNCH;

public class SessionFinishedEvent {
    Event event;

    public SessionFinishedEvent(String message) {
        event = new Event(EVENT_TITLE_LAUNCH, message);
    }

    public Event getEvent() {
        return event;
    }
}
