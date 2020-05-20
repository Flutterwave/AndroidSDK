package com.flutterwave.raveandroid.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_INSTRUCTION;

public class InstructionsDisplayedEvent {
    Event event;

    public InstructionsDisplayedEvent(String paymentMethod) {
        event = new Event(EVENT_TITLE_INSTRUCTION, paymentMethod + " Charge Instructions Displayed");
    }

    public Event getEvent() {
        return event;
    }
}
