package com.epipasha.cashflow;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.epipasha.cashflow.activities.AnalyticActivity;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.CategoryWithCashflow;
import com.epipasha.cashflow.objects.OperationType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.epipasha.cashflow.backup.BackupActivity;
import com.epipasha.cashflow.accounts.AccountActivity;
import com.epipasha.cashflow.categories.CategoryActivity;
import com.epipasha.cashflow.operations.OperationMasterActivity;
import com.epipasha.cashflow.activities.PreferencesActivity;
import com.epipasha.cashflow.accounts.AccountsFragment;
import com.epipasha.cashflow.categories.CategoriesFragment;
import com.epipasha.cashflow.operations.OperationsFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO Income, spending, cashflow

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewPager);
        FixesTabsPagerAdapter adapter = new FixesTabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            switch (tabs.getSelectedTabPosition()){
                case 0:{

                    break;
                }
                case 1: {
                    Intent i = new Intent(MainActivity.this, AccountActivity.class);
                    startActivity(i);
                    break;
                }
                case 2:{
                    Intent i = new Intent(MainActivity.this, CategoryActivity.class);
                    startActivity(i);
                    break;
                }
                case 3:{
                    Intent i = new Intent(MainActivity.this, OperationMasterActivity.class);
                    startActivity(i);
                    break;
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
                    return new HomeFragment();
                case 1:
                    return new AccountsFragment();
                case 2:
                    return new CategoriesFragment();
                case 3:
                    return new OperationsFragment();
                default:
                    throw new IllegalArgumentException("No such page");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Main";
                case 1:
                    return getString(R.string.Accounts);
                case 2:
                    return getString(R.string.Categories);
                case 3:
                    return getString(R.string.Operations);
                default:
                    return null;
            }
        }
    }

}