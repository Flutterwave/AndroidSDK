package com.flutterwave.raveandroid.rave_logger.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_SUBMIT;

public class SubmitEvent {
    Event event;

    public SubmitEvent(String detailsTitle) {
        event = new Event(EVENT_TITLE_SUBMIT, detailsTitle + " submitted");
    }

    public Event getEvent() {
        return event;
    }
}
