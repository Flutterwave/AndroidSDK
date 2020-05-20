package com.flutterwave.raveandroid;

public class ViewObject {

    int viewId;
    String data;
    Class<?> viewType;

    public ViewObject(int viewId, String data, Class<?> viewType) {
        this.viewId = viewId;
        this.data = data;
        this.viewType = viewType;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public Class<?> getViewType() {
        return viewType;
    }

    public void setViewType(Class<?> viewType) {
        this.viewType = viewType;
    }


}
