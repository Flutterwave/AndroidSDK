package com.flutterwave.raveandroid.data.events;

import static com.flutterwave.raveandroid.data.events.Event.EVENT_TITLE_SUBMIT;

public class SubmitEvent {
    Event event;

    public SubmitEvent(String detailsTitle) {
        event = new Event(EVENT_TITLE_SUBMIT, detailsTitle + " submitted");
    }

    public Event getEvent() {
        return event;
    }
}
