package com.flutterwave.raveandroid;

/**
 * Created by hamzafetuga on 28/11/2017.
 */

public class Meta {

    String metavalue;
    String metaname;

    public Meta(String metaname, String metavalue) {
        this.metaname = metaname;
        this.metavalue = metavalue;
    }

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
}
