package com.epipasha.cashflow.activities;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animatePreviousActivity();
    }

    private void animatePreviousActivity(){
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        animatePreviousActivity();
    }

}
