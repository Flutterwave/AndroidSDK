package com.flutterwave.raveandroid.card.savedcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.R;

public class SavedCardsActivity extends AppCompatActivity {
    public static final String ACTIVITY_MOTIVE = "activityMotive";
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null & getIntent().getIntExtra("theme", 0) != 0) {
            setTheme(getIntent().getIntExtra("theme", 0));
        }
        setContentView(R.layout.rave_sdk_activity_futher_verification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (findViewById(R.id.frame_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            fragment = new SavedCardsFragment();
            fragment.setArguments(getIntent().getExtras());

        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_container, fragment).commit();
        } else {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
