package com.flutterwave.raveandroid;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Guideline;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static android.support.constraint.ConstraintLayout.LayoutParams.HORIZONTAL;

public class NewRavePayActivity extends AppCompatActivity {

    View.OnClickListener onClickListener;
    private HashMap<Integer, Guideline> guidelineMap = new HashMap<>();
    private ArrayList<Tile> tiles = new ArrayList<>();
    private HashMap<Integer, Tile> tileMap = new HashMap<>();
    private Guideline topGuide;
    private Guideline bottomGuide;
    private int tileCount = 10;
    private ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rave_pay);
        root = findViewById(R.id.rave_pay_activity_root);


        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(view);
            }
        };

        topGuide = createGuideline(this, HORIZONTAL);
        root.addView(topGuide);
        topGuide.setGuidelinePercent(0.1f);

        bottomGuide = createGuideline(this, HORIZONTAL);
        root.addView(bottomGuide);
        bottomGuide.setGuidelinePercent(0.9f);

        generateGuides(tileCount);
        generateTiles(tileCount);
        render();

    }

    private void handleClick(View it) {
//int viewId = it.getId();

        Tile tile = tileMap.get(it.getId());

        if (tile.isTop) {
            render(true);
//            for (t in tiles)
            for (int i = 0; i < tiles.size(); i++) {
                Tile t = tiles.get(i);
                t.isTop = false;
                tileMap.put(t.view.getId(), t);
            }
        } else {

            for (int i = 0; i < tiles.size(); i++) {
                Tile t = tiles.get(i);
                t.isTop = t.view.getId() == it.getId();
                tileMap.put(t.view.getId(), t);
            }


            Integer tileId = null;
            Tile foundTile = null;
            Integer foundIndex = null;

//            for (i in 0 until tiles.count())
            for (int i = 0; i < tiles.size(); i++) {
                Tile current = tiles.get(i);

                if (current.view.getId() == it.getId()) {
                    tileId = current.view.getId();
                    foundTile = current;
                    foundIndex = i;
                    break;
                }
            }

            //view was found
            if (tileId != null &&
                    foundTile != null &&
                    foundIndex != null &&
                    tiles.size() != 0) {

                //render selected To Top
                renderToTop(foundTile.view);

                //more than one view but selected is last
                if (tileId == tiles.get(tiles.size() - 1).view.getId()) {
                    //pick first as bottom
                    int bottomId = tiles.get(0).view.getId();
//                            //pick penultimate as bottom
//                            renderToBottom(tiles[foundIndex - 1].view)
                    renderToBottom(tiles.get(0).view);
                    //hide other
                    for (int i = 0; i < tiles.size(); i++) {
                        Tile t = tiles.get(i);
                        if (it.getId() != t.view.getId() && bottomId != t.view.getId()) {
                            renderAsHidden(t.view);
                        }
                    }
                } else {

                    int bottomId = tiles.get(foundIndex + 1).view.getId();
                    //pick next as bottom
                    renderToBottom(tiles.get(foundIndex + 1).view);
                    //hide other
                    for (int i = 0; i < tiles.size(); i++) {
                        Tile t = tiles.get(i);
                        if (it.getId() != t.view.getId() && bottomId != t.view.getId()) {
                            renderAsHidden(t.view);
                        }
                    }
                }
            }
        }

    }


    private void renderToTop(View tv) {

        ConstraintSet set = new ConstraintSet();
        set.clone(root);

        set.connect(tv.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP);
        set.connect(tv.getId(), ConstraintSet.BOTTOM, topGuide.getId(), ConstraintSet.TOP);
        set.connect(tv.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
        set.connect(tv.getId(), ConstraintSet.RIGHT, root.getId(), ConstraintSet.RIGHT);
        set.constrainWidth(tv.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
        set.constrainHeight(tv.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(350);
            TransitionManager.beginDelayedTransition(root, transition);
            set.applyTo(root);
        } else set.applyTo(root);
    }

    private void renderToBottom(View tv) {

        ConstraintSet set = new ConstraintSet();
        set.clone(root);

        set.connect(tv.getId(), ConstraintSet.TOP, bottomGuide.getId(), ConstraintSet.BOTTOM);
        set.connect(tv.getId(), ConstraintSet.BOTTOM, root.getId(), ConstraintSet.BOTTOM);
        set.connect(tv.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
        set.connect(tv.getId(), ConstraintSet.RIGHT, root.getId(), ConstraintSet.RIGHT);
        set.constrainWidth(tv.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
        set.constrainHeight(tv.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(350);
            TransitionManager.beginDelayedTransition(root, transition);
            set.applyTo(root);
        } else set.applyTo(root);
    }

    private void renderAsHidden(View tv) {

        ConstraintSet set = new ConstraintSet();
        set.clone(root);

        set.connect(tv.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.BOTTOM);
        set.connect(tv.getId(), ConstraintSet.BOTTOM, root.getId(), ConstraintSet.BOTTOM);
        set.connect(tv.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
        set.connect(tv.getId(), ConstraintSet.RIGHT, root.getId(), ConstraintSet.RIGHT);
        set.constrainWidth(tv.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
        set.constrainHeight(tv.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(350);
            TransitionManager.beginDelayedTransition(root, transition);
            set.applyTo(root);
        } else set.applyTo(root);
    }

    private void generateTiles(int count) {

        for (int t = 0; t < count; t++) {
            View tileView = createTileView("tile" + t);
            Tile tile = new Tile(tileView, false);
            tiles.add(tile);
            tileView.setOnClickListener(onClickListener);
            tileMap.put(tileView.getId(), tile);
        }
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private void render() {
        render(false);
    }

    private void render(boolean animated) {

        ConstraintSet set = new ConstraintSet();
        set.clone(root);

//        for (i in 0 until tiles.count())
        for (int i = 0; i < tiles.size(); i++) {

            View tv2 = tiles.get(i).view;

            int upIndex = 10 - (i + 1);
            Guideline upGuide = guidelineMap.get(upIndex);
            Guideline downGuide = guidelineMap.get(upIndex + 1);

            if (upGuide != null && downGuide != null) {
                set.connect(tv2.getId(), ConstraintSet.TOP, upGuide.getId(), ConstraintSet.BOTTOM);
                set.connect(tv2.getId(), ConstraintSet.BOTTOM, downGuide.getId(), ConstraintSet.TOP);
                set.connect(tv2.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
                set.connect(tv2.getId(), ConstraintSet.RIGHT, root.getId(), ConstraintSet.RIGHT);
                set.constrainWidth(tv2.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
                set.constrainHeight(tv2.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
            }

        }

        if (animated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AutoTransition transition = new AutoTransition();
                transition.setDuration(350);
                TransitionManager.beginDelayedTransition(root, transition);
                set.applyTo(root);
            }
        } else {
            set.applyTo(root);
        }

    }

    private View createTileView(String title) {
        View tileView = getLayoutInflater().inflate(R.layout.payment_type_tile_layout, root, false);
        TextView tv2 = tileView.findViewById(R.id.rave_payment_type_title_textView);
        tileView.setId(ViewCompat.generateViewId());
//        tv2.setBackgroundColor(getRandomColor());
        tv2.setText(title);
        tv2.setTextSize(29f);
        root.addView(tileView);

        return tileView;
    }

    private void generateGuides(int count) {


        for (int i = 0; i <= count; i++) {
            Guideline guideline = createGuideline(this, HORIZONTAL);
            root.addView(guideline);
            double percent = (1 - (0.08 * i));
            guideline.setGuidelinePercent((float) percent);
            guideline.setTag(10 - i);
            guidelineMap.put(10 - i, guideline);
        }

    }

    private Guideline createGuideline(Context context, int orientation) {
        Guideline guideline = new Guideline(context);
        guideline.setId(ViewCompat.generateViewId());
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        lp.orientation = orientation;
        guideline.setLayoutParams(lp);

        return guideline;
    }


}



