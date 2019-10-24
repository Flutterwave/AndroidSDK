package com.flutterwave.raveandroid;

import android.widget.TextView;

public class Tile {

    TextView view;
    boolean isTop;

    Tile(TextView view) {
        this.view = view;
        this.isTop = false;
    }

    Tile(TextView view, boolean isTop) {
        this.view = view;
        this.isTop = isTop;
    }
}
