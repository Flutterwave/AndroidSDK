package com.flutterwave.raveandroid.data;

public class Event {
    public static String EVENT_TITLE_LAUNCHED = "Launched";
    public static String EVENT_TITLE_CANCELLED = "Cancelled";
    public static String EVENT_TITLE_FINISHED = "Finished";
    public static String EVENT_TITLE_MINIMIZED = "Minimized";
    public static String EVENT_TITLE_SELECTED_PAYMENT_METHOD = "Selected";
    String title;
    String message;

    public Event(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
