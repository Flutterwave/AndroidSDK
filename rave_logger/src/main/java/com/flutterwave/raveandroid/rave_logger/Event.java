package com.flutterwave.raveandroid.rave_logger;

public class Event {
    public static String EVENT_TITLE_LAUNCH = "Launched";
    public static String EVENT_TITLE_FINISH = "Session Finished";
    public static String EVENT_TITLE_SUBMIT = "Submit";
    public static String EVENT_TITLE_CHARGE = "Charge";
    public static String EVENT_TITLE_VALIDATE = "Validate";
    public static String EVENT_TITLE_TYPING = "Input";
    public static String EVENT_TITLE_ERROR = "Error";
    public static String EVENT_TITLE_REDIRECT = "Redirect";
    public static String EVENT_TITLE_REQUERY = "Requery";
    public static String EVENT_TITLE_FEE_DISPLAY_RESPONSE = "Fee Display Response";
    public static String EVENT_TITLE_INSTRUCTION = "Instruction DisplayED";
    public static String EVENT_TITLE_LIST_ITEM_SELECTED = "List Option Selected";

    String language = "Android";
    private String version = BuildConfig.VERSION_NAME; //version of the sdk
    private String publicKey;
    private String title;
    private String message;


    public Event(String title,
                 String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
