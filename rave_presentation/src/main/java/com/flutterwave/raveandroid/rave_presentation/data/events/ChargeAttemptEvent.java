package com.flutterwave.raveandroid.rave_presentation.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_CHARGE;

public class ChargeAttemptEvent {
    Event event;

    public ChargeAttemptEvent(String paymentMethod) {
        event = new Event(EVENT_TITLE_CHARGE, "Attempting " + paymentMethod + " Charge");
    }

    public Event getEvent() {
        return event;
    }
}
