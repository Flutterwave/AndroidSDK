package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_LAUNCH;

public class ScreenMinimizeEvent {
    Event event;

    public ScreenMinimizeEvent(String screenTitle) {
        event = new Event(EVENT_TITLE_LAUNCH, "Minimized " + screenTitle);
    }

    public Event getEvent() {
        return event;
    }
}
