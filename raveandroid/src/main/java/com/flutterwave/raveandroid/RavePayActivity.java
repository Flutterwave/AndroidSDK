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
import com.flutterwave.raveandroid.card.CardFragment;
import com.flutterwave.raveandroid.di.components.AppComponent;
import com.flutterwave.raveandroid.di.components.DaggerAppComponent;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyFragment;
import com.flutterwave.raveandroid.mpesa.MpesaFragment;
import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyFragment;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyFragment;
import com.flutterwave.raveandroid.zmmobilemoney.ZmMobileMoneyFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.RaveConstants.PERMISSIONS_REQUEST_READ_PHONE_STATE;
import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.RAVE_PARAMS;
import static com.flutterwave.raveandroid.RaveConstants.STAGING_URL;

public class RavePayActivity extends AppCompatActivity {

    ViewPager pager;
    TabLayout tabLayout;
    RelativeLayout permissionsRequiredLayout;
    View mainContent;
    Button requestPermsBtn;
    int theme;
    RavePayInitializer ravePayInitializer;
    MainPagerAdapter mainPagerAdapter;
    public static int RESULT_SUCCESS = 111;
    public static int RESULT_ERROR = 222;
    public static int RESULT_CANCELLED = 333;

    AppComponent appComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ravePayInitializer = Parcels.unwrap(getIntent().getParcelableExtra(RAVE_PARAMS));
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(RAVEPAY, "Error retrieving initializer");
        }

        buildGraph();

        theme = ravePayInitializer.getTheme();

        if (theme != 0) {
            try {
                setTheme(theme);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_rave_pay);

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

        if (ravePayInitializer.isWithCard()) {
            raveFragments.add(new RaveFragment(new CardFragment(), "Card"));
        }

        if (ravePayInitializer.isWithAccount()) {
            if (ravePayInitializer.getCountry().equalsIgnoreCase("us") && ravePayInitializer.getCurrency().equalsIgnoreCase("usd")) {
                raveFragments.add(new RaveFragment(new AchFragment(), "ACH"));
            }
            else if (ravePayInitializer.getCountry().equalsIgnoreCase("ng") && ravePayInitializer.getCurrency().equalsIgnoreCase("ngn")){
                raveFragments.add(new RaveFragment(new AccountFragment(), "Account"));
            }
        }

        if (ravePayInitializer.isWithMpesa()) {
            raveFragments.add(new RaveFragment(new MpesaFragment(), "Mpesa"));
        }

        if (ravePayInitializer.isWithGHMobileMoney()) {
            raveFragments.add(new RaveFragment(new GhMobileMoneyFragment(), "GHANA MOBILE MONEY"));
        }

        if (ravePayInitializer.isWithZmMobileMoney()) {
            raveFragments.add(new RaveFragment(new ZmMobileMoneyFragment(), "ZAMBIA MOBILE MONEY"));
        }

        if (ravePayInitializer.isWithUgMobileMoney()) {
            raveFragments.add(new RaveFragment(new UgMobileMoneyFragment(), "UGANDA MOBILE MONEY"));
        }

        if (ravePayInitializer.isWithRwfMobileMoney()) {
            raveFragments.add(new RaveFragment(new RwfMobileMoneyFragment(), "RWANDA MOBILE MONEY"));
        }

        if (ravePayInitializer.isWithBankTransfer()) {
            if (ravePayInitializer.getCountry().equalsIgnoreCase("ng") && ravePayInitializer.getCurrency().equalsIgnoreCase("ngn")){
                raveFragments.add(new RaveFragment(new BankTransferFragment(), "Bank Transfer"));
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

        String baseUrl;

        if (ravePayInitializer.isStaging()) {
            baseUrl = STAGING_URL;
        } else {
            baseUrl = LIVE_URL;
        }

        appComponent = DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .networkModule(new NetworkModule(baseUrl))
                .build();

        ((RaveApp) getApplication()).setAppComponent(appComponent);

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
        }
        else {
            permissionsRequiredLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onBackPressed() {

        setResult(RavePayActivity.RESULT_CANCELLED, new Intent());
        super.onBackPressed();
    }


}

