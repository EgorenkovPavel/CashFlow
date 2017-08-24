package com.epipasha.cashflow;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.epipasha.cashflow.fragments.AnaliticFragment;
import com.epipasha.cashflow.fragments.ListFragment;
import com.epipasha.cashflow.fragments.account.AccountFragment;
import com.epipasha.cashflow.fragments.account.AccountListFragment;
import com.epipasha.cashflow.fragments.category.CategoryFragment;
import com.epipasha.cashflow.fragments.goal.GoalFragment;
import com.epipasha.cashflow.fragments.operation.OperationFragment;
import com.epipasha.cashflow.fragments.summary.SummaryFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int OPEN_OPERATION_MASTER = 1;

    private Fragment frag;
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
                if(frag instanceof ListFragment){
                    ((ListFragment)frag).addInstance();
                }else if(frag instanceof SummaryFragment){
                    Intent i = new Intent();
                    i.setClass(view.getContext(), OperationMaster.class);
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

        navigationView.setCheckedItem(R.id.nav_summary);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_summary));

        if (Prefs.isShowOperationMasterOnStart(this)){
            Intent i = new Intent(this, OperationMaster.class);
            startActivity(i);
        }

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
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_summary) {
            setContantFragment(new SummaryFragment());
            getSupportActionBar().setTitle(item.getTitle());

        } else if (id == R.id.nav_accounts) {
            setContantFragment(new AccountFragment());
            getSupportActionBar().setTitle(item.getTitle());

        } else if (id == R.id.nav_categories) {
            setContantFragment(new CategoryFragment());
            getSupportActionBar().setTitle(item.getTitle());

        } else if (id == R.id.nav_operations) {
            setContantFragment(new OperationFragment());
            getSupportActionBar().setTitle(item.getTitle());

        } else if (id == R.id.nav_goals){
            setContantFragment(new GoalFragment());
            getSupportActionBar().setTitle(item.getTitle());

        } else if (id == R.id.nav_analitics){
            setContantFragment(new AnaliticFragment());
            getSupportActionBar().setTitle(item.getTitle());

        } else if (id == R.id.nav_operation_master){
            Intent i = new Intent(this, OperationMaster.class);
            startActivity(i);

        } else if (id == R.id.nav_settings){
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        }

        else if (id == R.id.nav_test){
            setContantFragment(new AccountListFragment());
            getSupportActionBar().setTitle(item.getTitle());
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

    private void setContantFragment(Fragment newFrag) {
        this.frag = newFrag;
        if(frag!=null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction tr = fm.beginTransaction();
            tr.replace(R.id.container, frag);
            tr.commit();
        }

    }

}