package com.epipasha.cashflow;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.epipasha.cashflow.fragments.account.AccountFragment;
import com.epipasha.cashflow.fragments.account.DetailAccountActivity;
import com.epipasha.cashflow.fragments.category.CategoryFragment;
import com.epipasha.cashflow.fragments.category.DetailCategoryActivity;
import com.epipasha.cashflow.fragments.operation.OperationFragment;

public class MainActivity extends AppCompatActivity{

    private static final String FRAGMENT_TAG = "fragment_tag";

    private TabLayout tabs;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                MainActivity.this.onTabSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        onTabSelected();

    }

    private void onTabSelected() {
        switch (tabs.getSelectedTabPosition()){
            case 0:{
                setContentFragment(new AccountFragment());
                break;
            }
            case 1:{
                setContentFragment(new CategoryFragment());
                break;
            }
            case 2:{
                setContentFragment(new OperationFragment());
                break;
            }
        }
    }

    private void setContentFragment(Fragment newFrag) {
        if(newFrag!=null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction tr = fm.beginTransaction();
            tr.replace(R.id.container, newFrag, FRAGMENT_TAG);
            tr.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Prefs.setSelectedTab(this, tabs.getSelectedTabPosition());
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
}