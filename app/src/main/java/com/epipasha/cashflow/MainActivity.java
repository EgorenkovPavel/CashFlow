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

import com.epipasha.cashflow.activities.DetailAccountActivity;
import com.epipasha.cashflow.activities.DetailCategoryActivity;
import com.epipasha.cashflow.fragments.AccountListFragment;
import com.epipasha.cashflow.fragments.CategoryListFragment;
import com.epipasha.cashflow.fragments.OperationListFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewPager);
        FixesTabsPagerAdapter adapter = new FixesTabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (tabs.getSelectedTabPosition()){
                    case 0: {
                        Intent i = new Intent(MainActivity.this, DetailAccountActivity.class);
                        startActivity(i);
                        break;
                    }
                    case 1:{
                        Intent i = new Intent(MainActivity.this, DetailCategoryActivity.class);
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

        public FixesTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new AccountListFragment();
                case 1:
                    return new CategoryListFragment();
                case 2:
                    return new OperationListFragment();
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