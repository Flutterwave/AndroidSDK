package com.flutterwave.raveandroid;


import android.view.View;

public class Tile {

    View view;
    boolean isTop;

    Tile(View view) {
        this.view = view;
        this.isTop = false;
    }

    Tile(View view, boolean isTop) {
        this.view = view;
        this.isTop = isTop;
    }
}
