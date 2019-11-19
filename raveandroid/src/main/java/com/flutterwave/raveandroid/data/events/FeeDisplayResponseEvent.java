package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_FEE_DISPLAY_RESPONSE;

public class FeeDisplayResponseEvent {
    Event event;

    public FeeDisplayResponseEvent(boolean isPositive) {
        if (isPositive) event = new Event(EVENT_TITLE_FEE_DISPLAY_RESPONSE, "Fee Confirmed");
        else event = new Event(EVENT_TITLE_FEE_DISPLAY_RESPONSE, "Fee Rejected");
    }

    public Event getEvent() {
        return event;
    }
}
