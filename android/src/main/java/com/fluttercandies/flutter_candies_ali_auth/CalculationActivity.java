package com.fluttercandies.flutter_candies_ali_auth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;

import androidx.annotation.Nullable;

public class CalculationActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(R.anim.in_activity,R.anim.out_activity);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_activity,R.anim.out_activity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.in_activity,R.anim.out_activity);

    }
}
