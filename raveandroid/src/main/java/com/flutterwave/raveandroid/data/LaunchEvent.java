package com.flutterwave.raveandroid.data;

import static com.flutterwave.raveandroid.data.Event.EVENT_TITLE_LAUNCHED;

public class LaunchEvent {
    Event event;

    public LaunchEvent(String screenTitle) {
        event = new Event(EVENT_TITLE_LAUNCHED, "Launched " + screenTitle);
    }

    public Event getEvent() {
        return event;
    }
}
