package com.flutterwave.raveandroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.flutterwave.raveandroid.account.AccountFragment;
import com.flutterwave.raveandroid.ach.AchFragment;
import com.flutterwave.raveandroid.banktransfer.BankTransferFragment;
import com.flutterwave.raveandroid.barter.BarterFragment;
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
import java.util.List;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ACCOUNT;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ACH;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_BANK_TRANSFER;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_BARTER;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_CARD;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_MPESA;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_UK;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_USSD;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PERMISSIONS_REQUEST_READ_PHONE_STATE;
import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.RAVE_PARAMS;
import static com.flutterwave.raveandroid.RaveConstants.STAGING_URL;

public class OldRavePayActivity extends AppCompatActivity {

    public static String BASE_URL;
    public static int RESULT_SUCCESS = 111;
    public static int RESULT_ERROR = 222;
    public static int RESULT_CANCELLED = 333;
    ViewPager pager;
    TabLayout tabLayout;
    RelativeLayout permissionsRequiredLayout;
    View mainContent;
    Button requestPermsBtn;
    int theme;
    RavePayInitializer ravePayInitializer;
    MainPagerAdapter mainPagerAdapter;
    AppComponent appComponent;
    ArrayList<Integer> orderedPaymentTypesList;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ravePayInitializer = Parcels.unwrap(getIntent().getParcelableExtra(RAVE_PARAMS));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(RAVEPAY, "Error retrieving initializer");
        }

        orderedPaymentTypesList = ravePayInitializer.getOrderedPaymentTypesList();
        buildGraph();

        theme = ravePayInitializer.getTheme();

        if (theme != 0) {
            try {
                setTheme(theme);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_old_rave_pay);

        if (ravePayInitializer.isStaging() && ravePayInitializer.getShowStagingLabel()) {
            findViewById(R.id.stagingModeBannerLay).setVisibility(View.VISIBLE);
        }


        tabLayout = findViewById(R.id.sliding_tabs);
        pager = findViewById(R.id.pager);
        permissionsRequiredLayout = findViewById(R.id.rave_permission_required_layout);
        mainContent = findViewById(R.id.main_content);
        requestPermsBtn = findViewById(R.id.requestPermsBtn);

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        List<RaveFragment> raveFragments = new ArrayList<>();

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_CARD)) {
            raveFragments.add(new RaveFragment(new CardFragment(), "Card"));
        }
        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_BARTER)) {
            raveFragments.add(new RaveFragment(new BarterFragment(), "Barter"));
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_ACH) || orderedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT)) {
            if (ravePayInitializer.getCountry().equalsIgnoreCase("us") && ravePayInitializer.getCurrency().equalsIgnoreCase("usd")) {
                raveFragments.add(new RaveFragment(new AchFragment(), "ACH"));
            } else if (ravePayInitializer.getCountry().equalsIgnoreCase("ng") && ravePayInitializer.getCurrency().equalsIgnoreCase("ngn")) {
                raveFragments.add(new RaveFragment(new AccountFragment(), "Account"));
            }
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_MPESA) && ravePayInitializer.getCurrency().equalsIgnoreCase("KES")) {
            raveFragments.add(new RaveFragment(new MpesaFragment(), "Mpesa"));
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY) && ravePayInitializer.getCurrency().equalsIgnoreCase("GHS")) {
            raveFragments.add(new RaveFragment(new GhMobileMoneyFragment(), "GHANA MOBILE MONEY"));
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY)) {
            raveFragments.add(new RaveFragment(new ZmMobileMoneyFragment(), "ZAMBIA MOBILE MONEY"));
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_ACH) && ravePayInitializer.getCurrency().equalsIgnoreCase("UGX")) {
            raveFragments.add(new RaveFragment(new UgMobileMoneyFragment(), "UGANDA MOBILE MONEY"));
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_UK)) {
            raveFragments.add(new RaveFragment(new UkFragment(), "UK"));
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY)) {
            raveFragments.add(new RaveFragment(new FrancMobileMoneyFragment(), "Franc Mobile Money"));
        }

        if (orderedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY)) {
            raveFragments.add(new RaveFragment(new RwfMobileMoneyFragment(), "RWANDA MOBILE MONEY"));
        }

        if (ravePayInitializer.getCountry().equalsIgnoreCase("ng") && ravePayInitializer.getCurrency().equalsIgnoreCase("ngn")) {
            if (orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER)) {
                raveFragments.add(new RaveFragment(new BankTransferFragment(), "Bank Transfer"));
            }
            if (orderedPaymentTypesList.contains(PAYMENT_TYPE_USSD)) {
                raveFragments.add(new RaveFragment(new UssdFragment(), "USSD"));
            }
        }

        mainPagerAdapter.setFragments(raveFragments);
        pager.setAdapter(mainPagerAdapter);

        tabLayout.setupWithViewPager(pager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkForRequiredPermissions();
        }

        requestPermsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
            }
        });

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mainContent.setVisibility(View.VISIBLE);
                permissionsRequiredLayout.setVisibility(GONE);

            } else {
                permissionsRequiredLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public RavePayInitializer getRavePayInitializer() {
        return ravePayInitializer;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkForRequiredPermissions() {

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsRequiredLayout.setVisibility(View.VISIBLE);
        } else {
            permissionsRequiredLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RavePayActivity.RESULT_CANCELLED, new Intent());
        super.onBackPressed();
    }


}

