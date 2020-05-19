package com.flutterwave.raveandroid;


import androidx.fragment.app.Fragment;

/**
 * Created by hamzafetuga on 21/07/2017.
 */

public class RaveFragment {
    Fragment fragment;
    String title;

    public Fragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }

    public RaveFragment(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

}
