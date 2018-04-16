package com.epipasha.cashflow.activities;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.epipasha.cashflow.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
