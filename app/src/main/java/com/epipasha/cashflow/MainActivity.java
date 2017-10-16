package com.epipasha.cashflow;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.epipasha.cashflow.fragments.AnalyticFragment;
import com.epipasha.cashflow.fragments.ListFragment;
import com.epipasha.cashflow.fragments.account.AccountFragment;
import com.epipasha.cashflow.fragments.category.CategoryFragment;
import com.epipasha.cashflow.fragments.operation.OperationFragment;
import com.epipasha.cashflow.fragments.summary.SummaryFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int OPEN_OPERATION_MASTER = 1;
    private static final String FRAGMENT_TAG = "fragment_tag";
    private static final String SAVED_STATE_KEY_ITEM_ID = "item_id";

    private int menuItemId;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                if (frag instanceof ListFragment) {
                    ((ListFragment) frag).addInstance();
                } else if (frag instanceof SummaryFragment) {
                    Intent i = new Intent();
                    i.setClass(view.getContext(), OperationMasterActivity.class);
                    startActivityForResult(i, OPEN_OPERATION_MASTER);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {

            navigationView.setCheckedItem(R.id.nav_summary);
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_summary));

            if (Prefs.isShowOperationMasterOnStart(this)) {
                Intent i = new Intent(this, OperationMasterActivity.class);
                startActivity(i);
            }
        }else{

            menuItemId = savedInstanceState.getInt(SAVED_STATE_KEY_ITEM_ID);
            setActionBarTitle(navigationView.getMenu().findItem(menuItemId).getTitle());

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_KEY_ITEM_ID, menuItemId);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        menuItemId = item.getItemId();

         if (menuItemId == R.id.nav_summary) {
            setContentFragment(new SummaryFragment());
            setActionBarTitle(item.getTitle());

        } else if (menuItemId == R.id.nav_accounts) {
            setContentFragment(new AccountFragment());
            //setContentFragment(new AccountListFragment());
            setActionBarTitle(item.getTitle());

        } else if (menuItemId == R.id.nav_categories) {
            setContentFragment(new CategoryFragment());
            setActionBarTitle(item.getTitle());

        } else if (menuItemId == R.id.nav_operations) {
            setContentFragment(new OperationFragment());
            setActionBarTitle(item.getTitle());

        } else if (menuItemId == R.id.nav_analytics){
            setContentFragment(new AnalyticFragment());
            setActionBarTitle(item.getTitle());

        } else if (menuItemId == R.id.nav_operation_master){
            Intent i = new Intent(this, OperationMasterActivity.class);
            startActivity(i);

        } else if (menuItemId == R.id.nav_settings){
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);

        } else if (menuItemId == R.id.nav_backup){
             setContentFragment(new BackupFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_OPERATION_MASTER)
            //if (resultCode == RESULT_OK)
        {
            navigationView.setCheckedItem(R.id.nav_summary);
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_summary));
        }
    }

    private void setActionBarTitle(CharSequence title){
        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setTitle(title);
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

}