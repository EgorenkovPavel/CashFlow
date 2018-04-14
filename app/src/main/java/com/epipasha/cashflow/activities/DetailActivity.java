package com.epipasha.cashflow.activities;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.epipasha.cashflow.R;

public abstract class DetailActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:{
                saveObject();
                return true;
            }
            case android.R.id.home:{
                finish();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public abstract void saveObject();

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
