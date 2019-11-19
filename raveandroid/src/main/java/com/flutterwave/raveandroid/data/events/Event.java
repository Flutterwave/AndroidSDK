package com.flutterwave.raveandroid.data.events;

public class Event {
    public static String EVENT_TITLE_LAUNCH = "Launched";
    public static String EVENT_TITLE_CANCEL = "Cancelled";
    public static String EVENT_TITLE_FINISH = "Finished";
    public static String EVENT_TITLE_MINIMIZE = "Minimized";
    public static String EVENT_TITLE_SUBMIT = "Submit";
    public static String EVENT_TITLE_CHARGE = "Charge";
    public static String EVENT_TITLE_TYPING = "Input";
    String title;
    String message;

    public Event(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
