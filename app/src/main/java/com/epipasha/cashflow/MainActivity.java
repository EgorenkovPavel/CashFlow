package com.epipasha.cashflow;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.epipasha.cashflow.backup.BackupActivity;
import com.epipasha.cashflow.accounts.AccountActivity;
import com.epipasha.cashflow.categories.CategoryActivity;
import com.epipasha.cashflow.operations.OperationMasterActivity;
import com.epipasha.cashflow.activities.PreferencesActivity;
import com.epipasha.cashflow.accounts.AccountsFragment;
import com.epipasha.cashflow.categories.CategoriesFragment;
import com.epipasha.cashflow.operations.OperationsFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewPager);
        FixesTabsPagerAdapter adapter = new FixesTabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (tabs.getSelectedTabPosition()){
                    case 0: {
                        Intent i = new Intent(MainActivity.this, AccountActivity.class);
                        startActivity(i);
                        break;
                    }
                    case 1:{
                        Intent i = new Intent(MainActivity.this, CategoryActivity.class);
                        startActivity(i);
                        break;
                    }
                    case 2:{
                        Intent i = new Intent(MainActivity.this, OperationMasterActivity.class);
                        startActivity(i);
                        break;
                    }
                }
            }
        });

        TabLayout.Tab tab = tabs.getTabAt(Prefs.getSelectedTab(this));
        if(tab != null)
            tab.select();

        if (Prefs.isShowOperationMasterOnStart(this)){
            Intent i = new Intent(MainActivity.this, OperationMasterActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent prefIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(prefIntent);
                return true;
            case R.id.backup:
                Intent backupIntent = new Intent(MainActivity.this, BackupActivity.class);
                startActivity(backupIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        animateOpenActivity();
    }

    private void animateOpenActivity(){
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }

    public class FixesTabsPagerAdapter extends FragmentPagerAdapter {

        FixesTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    //return new AccountsFragment();
                    return new AccountsFragment();
                case 1:
                    //return new CategoriesFragment();
                    return new CategoriesFragment();
                case 2:
                    //return new OperationsFragment();
                    return new OperationsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.Accounts);
                case 1:
                    return getString(R.string.Categories);
                case 2:
                    return getString(R.string.Operations);
                default:
                    return null;
            }
        }
    }

}