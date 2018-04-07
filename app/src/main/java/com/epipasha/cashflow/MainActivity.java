package com.epipasha.cashflow;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.fragments.Adapter;
import com.epipasha.cashflow.fragments.account.AccountAdapter;
import com.epipasha.cashflow.fragments.account.AccountFragment;
import com.epipasha.cashflow.fragments.account.DetailAccountActivity;
import com.epipasha.cashflow.fragments.category.CategoryAdapter;
import com.epipasha.cashflow.fragments.category.CategoryFragment;
import com.epipasha.cashflow.fragments.category.DetailCategoryActivity;
import com.epipasha.cashflow.fragments.operation.OperationAdapter;
import com.epipasha.cashflow.fragments.operation.OperationFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACCOUNT_LOADER_ID = 0;
    private static final int CATEGORY_LOADER_ID = 1;
    private static final int OPERATION_LOADER_ID = 2;

    private static final String FRAGMENT_TAG = "fragment_tag";

    private TabLayout tabs;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private Adapter mAdapter;

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

        initRecycledView();

        TabLayout.Tab tab = tabs.getTabAt(Prefs.getSelectedTab(this));
        tab.select();
        onTabSelected();

    }

    private void initRecycledView(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

            }

    private void onTabSelected() {
        switch (tabs.getSelectedTabPosition()){
            case 0:{
                mAdapter = new AccountAdapter(this);
                recyclerView.setAdapter(mAdapter);
                getSupportLoaderManager().restartLoader(ACCOUNT_LOADER_ID, null, this);

                break;
            }
            case 1:{
                mAdapter = new CategoryAdapter(this);
                recyclerView.setAdapter(mAdapter);
                getSupportLoaderManager().restartLoader(CATEGORY_LOADER_ID, null, this);

                break;
            }
            case 2:{
                mAdapter = new OperationAdapter(this);
                recyclerView.setAdapter(mAdapter);
                getSupportLoaderManager().restartLoader(OPERATION_LOADER_ID, null, this);

                break;
            }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id){
            case ACCOUNT_LOADER_ID:
                return new CursorLoader(
                        this,
                        CashFlowContract.AccountEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            case CATEGORY_LOADER_ID:
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);

                return new CursorLoader(
                        this,
                        CashFlowContract.CategoryEntry.buildCategoryCostUri(year, month),
                        null,
                        null,
                        null,
                        null);
            case OPERATION_LOADER_ID:
                return new CursorLoader(
                        this,
                        CashFlowContract.OperationEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}