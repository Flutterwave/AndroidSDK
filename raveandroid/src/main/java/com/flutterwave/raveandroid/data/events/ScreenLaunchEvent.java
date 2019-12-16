package com.flutterwave.raveandroid.data.events;


import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_LAUNCH;

public class ScreenLaunchEvent {
    Event event;

    public ScreenLaunchEvent(String screenTitle) {
        event = new Event(EVENT_TITLE_LAUNCH, "Launched " + screenTitle);
    }

    public Event getEvent() {
        return event;
    }
}
