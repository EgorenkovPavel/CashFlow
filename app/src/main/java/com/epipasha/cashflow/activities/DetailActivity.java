package com.epipasha.cashflow.activities;

import android.view.Menu;
import android.view.MenuItem;

import com.epipasha.cashflow.R;

public abstract class DetailActivity extends BaseActivity {

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

}
