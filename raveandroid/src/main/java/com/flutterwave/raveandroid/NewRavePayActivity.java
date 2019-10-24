package com.flutterwave.raveandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Guideline;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
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
    RavePayInitializer ravePayInitializer;
    private ConstraintLayout root;
    private int tileCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rave_pay);
        root = findViewById(R.id.rave_pay_activity_root);

        setupRavePayInitializer();
        // Todo: Handle edge cases for too few (1) or too many (13) payment types
        // todo: Handle several screen sizes


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

        generatePaymentTiles(tileCount);
        generateGuides(tileCount);
        render();

    }

    private void setupRavePayInitializer() {
        ravePayInitializer = new RavePayInitializer(
                "user@example.com",
                100.00,
                "asdf",
                "asdfa",
                "1",
                "",
                "NGN",
                "NG",
                "Wuraola",
                "Benson",
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                R.style.DefaultTheme,
                false,
                0,
                0,
                true,
                "",
                "",
                "",
                false,
                true,
                true);
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

            renderAsHidden(root.getViewById(R.id.title_container));
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

    private void generatePaymentTiles(int count) {
// Todo: reintroduce currency checks
        if (ravePayInitializer.isWithCard()) {
            addPaymentType(RaveConstants.PAYMENT_TYPE_CARD);
        }

        if (ravePayInitializer.isWithAccount()) {
//            if (ravePayInitializer.getCountry().equalsIgnoreCase("us") && ravePayInitializer.getCurrency().equalsIgnoreCase("usd")) {
            addPaymentType(RaveConstants.PAYMENT_TYPE_ACH);
//            } else if (ravePayInitializer.getCountry().equalsIgnoreCase("ng") && ravePayInitializer.getCurrency().equalsIgnoreCase("ngn")) {
            addPaymentType(RaveConstants.PAYMENT_TYPE_ACCOUNT);
//            }
        }

        if (ravePayInitializer.isWithMpesa()
//                && ravePayInitializer.getCurrency().equalsIgnoreCase("KES")
        ) {
            addPaymentType(RaveConstants.PAYMENT_TYPE_MPESA);
        }

//        if (ravePayInitializer.isWithGHMobileMoney()
////                && ravePayInitializer.getCurrency().equalsIgnoreCase("GHS")
//        ) {
//            addPaymentType(RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY);
//        }
//
//        if (ravePayInitializer.isWithZmMobileMoney()) {
//            addPaymentType(RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY);
//        }
//
//        if (ravePayInitializer.isWithUgMobileMoney()
////                && ravePayInitializer.getCurrency().equalsIgnoreCase("UGX")
//        ) {
//            addPaymentType(RaveConstants.PAYMENT_TYPE_UG_MOBILE_MONEY);
//        }
//
//        if (ravePayInitializer.isWithUk()) {
//            addPaymentType(RaveConstants.PAYMENT_TYPE_UK);
//        }
//
//        if (ravePayInitializer.isWithFrancMobileMoney()) {
//            addPaymentType(RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY);
//        }
//
//        if (ravePayInitializer.isWithRwfMobileMoney()) {
//            addPaymentType(RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY);
//        }

//        if (ravePayInitializer.getCountry().equalsIgnoreCase("ng") && ravePayInitializer.getCurrency().equalsIgnoreCase("ngn")) {
        if (ravePayInitializer.isWithBankTransfer()) {
            addPaymentType(RaveConstants.PAYMENT_TYPE_BANK_TRANSFER);
        }
        if (ravePayInitializer.isWithUssd()) {
            addPaymentType(RaveConstants.PAYMENT_TYPE_USSD);
        }
//        }
    }

    private void addPaymentType(int paymentType) {
        View tileView;
        try {


            tileView = createPaymentTileView(RaveConstants.paymentTypesNamesList.get(paymentType) + "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Add payment type error", "Payment type doesn't exist.");
            return;
        }

        Tile tile = new Tile(tileView, false);
        tiles.add(tile);
        tileView.setOnClickListener(onClickListener);
        tileMap.put(tileView.getId(), tile);
        tileCount += 1;
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

        // Set title view
        View titleView = root.findViewById(R.id.title_container);
        if (titleView == null) {
            titleView = getLayoutInflater().inflate(R.layout.rave_payment_title_layout, root, false);
            root.addView(titleView);
        }
        set.connect(titleView.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP);
        set.connect(titleView.getId(), ConstraintSet.BOTTOM, guidelineMap.get(10 - tiles.size() + 1).getId(), ConstraintSet.BOTTOM);
        set.connect(titleView.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
        set.connect(titleView.getId(), ConstraintSet.RIGHT, root.getId(), ConstraintSet.RIGHT);
        set.constrainWidth(titleView.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
        set.constrainHeight(titleView.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

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

    private View createPaymentTileView(String title) {
        View tileView = getLayoutInflater().inflate(R.layout.payment_type_tile_layout, root, false);
        TextView tv2 = tileView.findViewById(R.id.rave_payment_type_title_textView);
        tileView.setId(ViewCompat.generateViewId());
//        tv2.setBackgroundColor(getRandomColor());
        String fullTitle = "Pay with " + title;

        SpannableStringBuilder sb = new SpannableStringBuilder(fullTitle);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 9, fullTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold

        tv2.setText(sb);
        tv2.setTextSize(20f);
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



