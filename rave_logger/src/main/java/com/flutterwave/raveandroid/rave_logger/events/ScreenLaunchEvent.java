package com.flutterwave.raveandroid.rave_logger.events;


import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_LAUNCH;

public class ScreenLaunchEvent {
    Event event;

    public ScreenLaunchEvent(String screenTitle) {
        event = new Event(EVENT_TITLE_LAUNCH, "Launched " + screenTitle);
    }

    public Event getEvent() {
        return event;
    }
}
