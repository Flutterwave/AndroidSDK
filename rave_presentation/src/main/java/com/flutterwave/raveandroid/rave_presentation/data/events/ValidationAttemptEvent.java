package com.flutterwave.raveandroid.rave_presentation.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_VALIDATE;

public class ValidationAttemptEvent {
    Event event;

    public ValidationAttemptEvent(String paymentMethod) {
        event = new Event(EVENT_TITLE_VALIDATE, "Attempting " + paymentMethod + " Payment Validation");
    }

    public Event getEvent() {
        return event;
    }
}
