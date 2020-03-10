package com.flutterwave.raveandroid.card.savedcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.flutterwave.raveandroid.R;

public class SavedCardsActivity extends AppCompatActivity {
    public static final String ACTIVITY_MOTIVE = "activityMotive";
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rave_activity_futher_verification);
        if (getIntent() != null & getIntent().getIntExtra("theme", 0) != 0) {
            setTheme(getIntent().getIntExtra("theme", 0));
        }

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
