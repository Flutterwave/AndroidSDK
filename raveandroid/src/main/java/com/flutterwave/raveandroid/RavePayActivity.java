package com.flutterwave.raveandroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Guideline;
import android.support.transition.AutoTransition;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.flutterwave.raveandroid.account.AccountFragment;
import com.flutterwave.raveandroid.ach.AchFragment;
import com.flutterwave.raveandroid.banktransfer.BankTransferFragment;
import com.flutterwave.raveandroid.card.CardFragment;
import com.flutterwave.raveandroid.di.components.AppComponent;
import com.flutterwave.raveandroid.di.components.DaggerAppComponent;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.francMobileMoney.FrancMobileMoneyFragment;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyFragment;
import com.flutterwave.raveandroid.mpesa.MpesaFragment;
import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyFragment;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyFragment;
import com.flutterwave.raveandroid.uk.UkFragment;
import com.flutterwave.raveandroid.ussd.UssdFragment;
import com.flutterwave.raveandroid.zmmobilemoney.ZmMobileMoneyFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static android.support.constraint.ConstraintLayout.LayoutParams.HORIZONTAL;
import static com.flutterwave.raveandroid.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ACCOUNT;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ACH;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_BANK_TRANSFER;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_MPESA;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_UG_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_UK;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_USSD;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PERMISSIONS_REQUEST_READ_PHONE_STATE;
import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.RAVE_PARAMS;
import static com.flutterwave.raveandroid.RaveConstants.STAGING_URL;

public class RavePayActivity extends AppCompatActivity {

    public static String BASE_URL;
    View.OnClickListener onClickListener;
    private HashMap<Integer, Guideline> guidelineMap = new HashMap<>();
    private ArrayList<PaymentTile> paymentTiles = new ArrayList<>();
    private HashMap<Integer, PaymentTile> tileMap = new HashMap<>();
    private Guideline topGuide;
    RavePayInitializer ravePayInitializer;
    private Guideline bottomGuide;
    private ConstraintLayout root;


    public static int RESULT_SUCCESS = 111;
    public static int RESULT_ERROR = 222;
    public static int RESULT_CANCELLED = 333;
    private int tileCount = 0;
    int theme;
    private float paymentTilesTextSize;
    View permissionsRequiredLayout;


    public AppComponent getAppComponent() {
        return appComponent;
    }

    AppComponent appComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rave_pay);
        root = findViewById(R.id.rave_pay_activity_root);

        try {
            ravePayInitializer = Parcels.unwrap(getIntent().getParcelableExtra(RAVE_PARAMS));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(RAVEPAY, "Error retrieving initializer");
        }


        buildGraph();
        theme = ravePayInitializer.getTheme();

        if (theme != 0) {
            try {
                setTheme(theme);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForRequiredPermissions();
        } else setupMainContent();

    }

    private void setupMainContent() {
        tileCount = ravePayInitializer.getOrderedPaymentTypesList().size();
        if (tileCount > 8) paymentTilesTextSize = 18f;
        else paymentTilesTextSize = 20f;
        // Todo: Handle edge cases for too many (13) payment types
        // todo: Handle several screen sizes


        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(view);
            }
        };

        topGuide = createGuideline(this, HORIZONTAL);
        root.addView(topGuide);
        topGuide.setGuidelinePercent(0.08f);

        bottomGuide = createGuideline(this, HORIZONTAL);
        root.addView(bottomGuide);
        bottomGuide.setGuidelinePercent(0.92f);

        generatePaymentTiles();
        generateGuides(tileCount);
        render();
    }


    private void handleClick(View clickedView) {

        PaymentTile paymentTile = tileMap.get(clickedView.getId());

        if (paymentTile.isTop) {
            showAllPaymentTypes();
        } else {

            showSelectedPaymentType(clickedView);
        }

    }

    private void showAllPaymentTypes() {
        render(true);
        for (int i = 0; i < paymentTiles.size(); i++) {
            PaymentTile t = paymentTiles.get(i);
            t.isTop = false;
            tileMap.put(t.view.getId(), t);
        }
    }

    private void showSelectedPaymentType(View clickedView) {
        for (int i = 0; i < paymentTiles.size(); i++) {
            PaymentTile t = paymentTiles.get(i);
            t.isTop = t.view.getId() == clickedView.getId();
            tileMap.put(t.view.getId(), t);
        }


        Integer tileId = null;
        PaymentTile foundPaymentTile = null;
        Integer foundIndex = null;

        for (int i = 0; i < paymentTiles.size(); i++) {
            PaymentTile current = paymentTiles.get(i);

            if (current.view.getId() == clickedView.getId()) {
                tileId = current.view.getId();
                foundPaymentTile = current;
                foundIndex = i;
                break;
            }
        }

        // If view was found
        if (tileId != null && paymentTiles.size() != 0) {

            renderAsHidden(root.getViewById(R.id.title_container)); // Hide title layout

            //render selected view To Top
            renderToTop(foundPaymentTile.view);

            displayPaymentFragment(foundPaymentTile);


            //If more than one view
            if (tileCount > 1) {
                //If selected view is last
                if (tileId == paymentTiles.get(paymentTiles.size() - 1).view.getId()) {
                    //pick first as bottom
                    int bottomId = paymentTiles.get(0).view.getId();
                    renderToBottom(paymentTiles.get(0).view);
                    //hide other(s)
                    for (int i = 0; i < paymentTiles.size(); i++) {
                        PaymentTile t = paymentTiles.get(i);
                        if (clickedView.getId() != t.view.getId() && bottomId != t.view.getId()) {
                            renderAsHidden(t.view);
                        }
                    }
                } else {

                    int bottomId = paymentTiles.get(foundIndex + 1).view.getId();
                    //pick next as bottom
                    renderToBottom(paymentTiles.get(foundIndex + 1).view);
                    //hide other(S)
                    for (int i = 0; i < paymentTiles.size(); i++) {
                        PaymentTile t = paymentTiles.get(i);
                        if (clickedView.getId() != t.view.getId() && bottomId != t.view.getId()) {
                            renderAsHidden(t.view);
                        }
                    }
                }
            }
        }
    }

    private void displayPaymentFragment(final PaymentTile foundPaymentTile) {
        View fragmentContainerLayout = root.getViewById(R.id.payment_fragment_container_layout);
        if (fragmentContainerLayout == null) {
            fragmentContainerLayout = getLayoutInflater().inflate(R.layout.payment_fragment_container_layout, root, false);
            root.addView(fragmentContainerLayout);
            fragmentContainerLayout.findViewById(R.id.choose_another_payment_method_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAllPaymentTypes();
                }
            });
        }
        if (ravePayInitializer.isStaging() && ravePayInitializer.getShowStagingLabel()) {
            findViewById(R.id.stagingModeBannerLay).setVisibility(View.VISIBLE);
        }

        // Todo: Handle payment fragment switching fluidly
//        int topToBottomConstraint = ((ConstraintLayout.LayoutParams) fragmentContainerLayout.getLayoutParams()).topToBottom;
//        int topToTopConstraint = ((ConstraintLayout.LayoutParams) fragmentContainerLayout.getLayoutParams()).topToTop;
//        if (topToBottomConstraint != topGuide.getId() && topToTopConstraint != topGuide.getId()) {
//            setPaymentFragmentInPlace(fragmentContainerLayout, foundPaymentTile);
//        } else hideThenShowFragment(fragmentContainerLayout, foundPaymentTile);

        setPaymentFragmentInPlace(fragmentContainerLayout, foundPaymentTile);
    }

    private void hideThenShowFragment(final View layout, final PaymentTile paymentTile) {
        Transition.TransitionListener transitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                setPaymentFragmentInPlace(layout, paymentTile);

            }

            @Override
            public void onTransitionCancel(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionPause(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionResume(@NonNull Transition transition) {

            }
        };

        renderAsHidden(layout, transitionListener);
    }

    private void setPaymentFragmentInPlace(View fragmentContainerLayout, PaymentTile foundPaymentTile) {
        ConstraintSet set = new ConstraintSet();
        set.clone(root);

        set.connect(fragmentContainerLayout.getId(), ConstraintSet.TOP, topGuide.getId(), ConstraintSet.TOP);
        if (tileCount > 1)
            set.connect(fragmentContainerLayout.getId(), ConstraintSet.BOTTOM, bottomGuide.getId(), ConstraintSet.BOTTOM);
        else
            set.connect(fragmentContainerLayout.getId(), ConstraintSet.BOTTOM, root.getId(), ConstraintSet.BOTTOM);
        set.connect(fragmentContainerLayout.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
        set.connect(fragmentContainerLayout.getId(), ConstraintSet.RIGHT, root.getId(), ConstraintSet.RIGHT);
        set.constrainWidth(fragmentContainerLayout.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
        set.constrainHeight(fragmentContainerLayout.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

        addFragmentToLayout(foundPaymentTile);
        if (tileCount == 1)
            fragmentContainerLayout.findViewById(R.id.choose_another_payment_method_tv).setVisibility(View.GONE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(350);
            TransitionManager.beginDelayedTransition(root, transition);
            set.applyTo(root);
        } else {
            set.applyTo(root);
        }
    }

    private void addFragmentToLayout(PaymentTile foundPaymentTile) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (foundPaymentTile.paymentType) {
            case PAYMENT_TYPE_ACCOUNT:
                transaction.replace(R.id.payment_fragment_container, new AccountFragment());
                break;
            case PAYMENT_TYPE_ACH:
                transaction.replace(R.id.payment_fragment_container, new AchFragment());
                break;
            case PAYMENT_TYPE_BANK_TRANSFER:
                transaction.replace(R.id.payment_fragment_container, new BankTransferFragment());
                break;
            case RaveConstants.PAYMENT_TYPE_CARD:
                transaction.replace(R.id.payment_fragment_container, new CardFragment());
                break;
            case PAYMENT_TYPE_FRANCO_MOBILE_MONEY:
                transaction.replace(R.id.payment_fragment_container, new FrancMobileMoneyFragment());
                break;
            case PAYMENT_TYPE_GH_MOBILE_MONEY:
                transaction.replace(R.id.payment_fragment_container, new GhMobileMoneyFragment());
                break;
            case PAYMENT_TYPE_MPESA:
                transaction.replace(R.id.payment_fragment_container, new MpesaFragment());
                break;
            case PAYMENT_TYPE_RW_MOBILE_MONEY:
                transaction.replace(R.id.payment_fragment_container, new RwfMobileMoneyFragment());
                break;
            case PAYMENT_TYPE_UG_MOBILE_MONEY:
                transaction.replace(R.id.payment_fragment_container, new UgMobileMoneyFragment());
                break;
            case PAYMENT_TYPE_UK:
                transaction.replace(R.id.payment_fragment_container, new UkFragment());
                break;
            case PAYMENT_TYPE_USSD:
                transaction.replace(R.id.payment_fragment_container, new UssdFragment());
                break;
            case PAYMENT_TYPE_ZM_MOBILE_MONEY:
                transaction.replace(R.id.payment_fragment_container, new ZmMobileMoneyFragment());
                break;
            default:
                Log.d("Adding Payment Fragment", "Payment type does not exist in payment types list");
                render();// Show default view
                return;
        }

        transaction.commit();
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
        } else
            set.applyTo(root);
    }

    private void renderAsHidden(View view) {
        renderAsHidden(view, null);
    }

    private void renderAsHidden(View view, Transition.TransitionListener transitionListener) {
        if (view != null) {
            ConstraintSet set = new ConstraintSet();
            set.clone(root);

            set.connect(view.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP);
            set.connect(view.getId(), ConstraintSet.BOTTOM, root.getId(), ConstraintSet.TOP);
            set.connect(view.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
            set.connect(view.getId(), ConstraintSet.RIGHT, root.getId(), ConstraintSet.RIGHT);
            set.constrainWidth(view.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);
            set.constrainHeight(view.getId(), ConstraintSet.MATCH_CONSTRAINT_SPREAD);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AutoTransition transition = new AutoTransition();
                transition.setDuration(350);
                if (transitionListener != null) transition.addListener(transitionListener);
                TransitionManager.beginDelayedTransition(root, transition);
                set.applyTo(root);
            } else {
                set.applyTo(root);
                transitionListener.onTransitionEnd(null);
            }
        }
    }

    private void generatePaymentTiles() {
        String currency = ravePayInitializer.getCurrency();

        ArrayList<Integer> orderedPaymentTypesList = ravePayInitializer.getOrderedPaymentTypesList();
        // Reverse payment types order since payment types are added from the bottom
        Collections.reverse(orderedPaymentTypesList);

        for (int index = 0; index < orderedPaymentTypesList.size(); index++) {

            // Currency checks
            switch (orderedPaymentTypesList.get(index)) {
                case PAYMENT_TYPE_ACCOUNT:
                case PAYMENT_TYPE_BANK_TRANSFER:
                case PAYMENT_TYPE_USSD:
                    if (currency.equalsIgnoreCase("NGN"))
                        addPaymentType(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_ACH:
                    if (currency.equalsIgnoreCase("USD"))
                        addPaymentType(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_GH_MOBILE_MONEY:
                    if (currency.equalsIgnoreCase("GHS"))
                        addPaymentType(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_MPESA:
                    if (currency.equalsIgnoreCase("KES"))
                        addPaymentType(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_UG_MOBILE_MONEY:
                    if (currency.equalsIgnoreCase("UGX"))
                        addPaymentType(orderedPaymentTypesList.get(index));
                    break;
                default:
                    addPaymentType(orderedPaymentTypesList.get(index));
                    return;
            }
        }
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

        PaymentTile paymentTile = new PaymentTile(tileView, paymentType, false);
        paymentTiles.add(paymentTile);
        tileView.setOnClickListener(onClickListener);
        tileMap.put(tileView.getId(), paymentTile);
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private void render() {
        render(false);
    }

    private void render(boolean animated) {
        if (tileCount == 1) {
            View singlePaymentTileView = paymentTiles.get(0).view;
            singlePaymentTileView.callOnClick(); // Show payment fragment
//            singlePaymentTileView.findViewById(R.id.arrowIv2).setVisibility(View.GONE);
            ((TextView) singlePaymentTileView.findViewById(R.id.rave_payment_type_title_textView)).setGravity(Gravity.CENTER);

            singlePaymentTileView.setOnClickListener(null);
        } else {
            // Hide payment fragment container
            // Do this first before cloning the constraint set, otherwise, the changes here will be reversed
            View fragmentContainerLayout = root.getViewById(R.id.payment_fragment_container_layout);
            if (fragmentContainerLayout != null) {
                renderAsHidden(fragmentContainerLayout);
            }

            ConstraintSet set = new ConstraintSet();
            set.clone(root);

            for (int i = 0; i < paymentTiles.size(); i++) {

                View tv2 = paymentTiles.get(i).view;

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
            set.connect(titleView.getId(), ConstraintSet.BOTTOM, guidelineMap.get(10 - paymentTiles.size() + 1).getId(), ConstraintSet.BOTTOM);
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
    }

    private View createPaymentTileView(String title) {
        View tileView = getLayoutInflater().inflate(R.layout.payment_type_tile_layout, root, false);
        TextView tv2 = tileView.findViewById(R.id.rave_payment_type_title_textView);
        tileView.setId(ViewCompat.generateViewId());
        String fullTitle = "Pay with " + title;

        SpannableStringBuilder sb = new SpannableStringBuilder(fullTitle);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 9, fullTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        tv2.setText(sb);
        tv2.setTextSize(paymentTilesTextSize);
        root.addView(tileView);

        return tileView;
    }

    private void generateGuides(int count) {


        for (int i = 0; i <= count; i++) {
            Guideline guideline = createGuideline(this, HORIZONTAL);
            root.addView(guideline);
            double percent;
            if (count > 8) percent = (1 - (0.07 * i));
            else percent = (1 - (0.08 * i));
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

    private void buildGraph() {

        if (ravePayInitializer.isStaging()) {
            BASE_URL = STAGING_URL;
        } else {
            BASE_URL = LIVE_URL;
        }

        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .networkModule(new NetworkModule(BASE_URL))
                .build();
    }

    public RavePayInitializer getRavePayInitializer() {
        return ravePayInitializer;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                permissionsRequiredLayout.setVisibility(View.GONE);
                setupMainContent();

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkForRequiredPermissions() {

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsRequiredLayout = findViewById(R.id.rave_permission_required_layout);
            if (permissionsRequiredLayout != null)
                permissionsRequiredLayout.setVisibility(View.VISIBLE);
            else {
                root.addView(getLayoutInflater().inflate(R.layout.request_permissions_layout, root, false));
                permissionsRequiredLayout = findViewById(R.id.rave_permission_required_layout);
            }

            findViewById(R.id.requestPermsBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                                PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                }
            });
        } else {
            setupMainContent();
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RavePayActivity.RESULT_CANCELLED, new Intent());
        super.onBackPressed();
    }

}