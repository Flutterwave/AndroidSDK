package com.flutterwave.raveandroid.data.events;

import com.flutterwave.raveandroid.rave_logger.Event;

import static com.flutterwave.raveandroid.rave_logger.Event.EVENT_TITLE_LIST_ITEM_SELECTED;

public class ListItemSelectedEvent {
    Event event;

    public ListItemSelectedEvent(String itemType) {
        event = new Event(EVENT_TITLE_LIST_ITEM_SELECTED, itemType + " Selected");
    }

    public Event getEvent() {
        return event;
    }
}
