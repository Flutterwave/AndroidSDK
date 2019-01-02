package com.flutterwave.raveandroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.flutterwave.raveandroid.account.AccountFragment;
import com.flutterwave.raveandroid.ach.AchFragment;
import com.flutterwave.raveandroid.card.CardFragment;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyFragment;
import com.flutterwave.raveandroid.mpesa.MpesaFragment;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.PERMISSIONS_REQUEST_READ_PHONE_STATE;
import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.RAVE_PARAMS;

public class RavePayActivity extends AppCompatActivity {

    ViewPager pager;
    TabLayout tabLayout;
    RelativeLayout permissionsRequiredLayout;
    View mainContent;
    Button requestPermsBtn;
    int theme;
    RavePayInitializer ravePayInitializer;
    MainPagerAdapter mainPagerAdapter;
    static String secretKey;
    public static String BASE_URL;
    public static int RESULT_SUCCESS = 111;
    public static int RESULT_ERROR = 222;
    public static int RESULT_CANCELLED = 333;

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

        if (ravePayInitializer.isStaging()) {
            BASE_URL = RaveConstants.STAGING_URL;
        }
        else {
            BASE_URL = RaveConstants.LIVE_URL;
        }

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        permissionsRequiredLayout = (RelativeLayout) findViewById(R.id.rave_permission_required_layout);
        mainContent = findViewById(R.id.main_content);
        requestPermsBtn = (Button) findViewById(R.id.requestPermsBtn);

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

        if (ravePayInitializer.isWithUgMobileMoney()) {
            raveFragments.add(new RaveFragment(new UgMobileMoneyFragment(), "UGANDA MOBILE MONEY"));
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

