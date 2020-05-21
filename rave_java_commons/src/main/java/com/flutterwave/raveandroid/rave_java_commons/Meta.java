package com.flutterwave.raveandroid.rave_java_commons;

/**
 * Created by hamzafetuga on 28/11/2017.
 */

public class Meta {

    public Meta(String metaname, String metavalue) {
        this.metaname = metaname;
        this.metavalue = metavalue;
    }

    String metavalue;

    public String getMetavalue() {
        return metavalue;
    }

    public void setMetavalue(String metavalue) {
        this.metavalue = metavalue;
    }

    public String getMetaname() {
        return metaname;
    }

    public void setMetaname(String metaname) {
        this.metaname = metaname;
    }

    String metaname;
}
