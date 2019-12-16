package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_CHARGE;

public class ChargeAttemptEvent {
    Event event;

    public ChargeAttemptEvent(String paymentMethod) {
        event = new Event(EVENT_TITLE_CHARGE, "Attempting " + paymentMethod + " Charge");
    }

    public Event getEvent() {
        return event;
    }
}
