package com.flutterwave.raveandroid.data;

public class Event {
    public static String EVENT_TITLE_LAUNCHED = "Launched";
    public static String EVENT_TITLE_CANCELLED = "Cancelled";
    public static String EVENT_TITLE_FINISHED = "Finished";
    String title;
    String message;

    public Event(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
