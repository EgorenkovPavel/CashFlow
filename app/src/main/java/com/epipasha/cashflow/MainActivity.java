package com.epipasha.cashflow;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.adapters.Adapter;
import com.epipasha.cashflow.adapters.AccountAdapter;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.activities.DetailAccountActivity;
import com.epipasha.cashflow.adapters.CategoryAdapter;
import com.epipasha.cashflow.activities.DetailCategoryActivity;
import com.epipasha.cashflow.adapters.OperationAdapter;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACCOUNT_LOADER_ID = 0;
    private static final int CATEGORY_LOADER_ID = 1;
    private static final int OPERATION_LOADER_ID = 2;

    private TabLayout tabs;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private Adapter mAdapter;
    private ItemTouchHelper operationItemTouchHelper;

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

        if (Prefs.isShowOperationMasterOnStart(this)){
            Intent i = new Intent(MainActivity.this, OperationMasterActivity.class);
            startActivity(i);
        }

    }

    private void initRecycledView(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        operationItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = CashFlowContract.OperationEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(OPERATION_LOADER_ID, null, MainActivity.this);

            }
        });
    }

    private void onTabSelected() {
        switch (tabs.getSelectedTabPosition()){
            case 0:{
                mAdapter = new AccountAdapter(this);
                recyclerView.setAdapter(mAdapter);
                getSupportLoaderManager().restartLoader(ACCOUNT_LOADER_ID, null, this);

                operationItemTouchHelper.attachToRecyclerView(null);
                break;
            }
            case 1:{
                mAdapter = new CategoryAdapter(this);
                recyclerView.setAdapter(mAdapter);
                getSupportLoaderManager().restartLoader(CATEGORY_LOADER_ID, null, this);

                operationItemTouchHelper.attachToRecyclerView(null);
                break;
            }
            case 2:{
                mAdapter = new OperationAdapter(this);
                recyclerView.setAdapter(mAdapter);
                getSupportLoaderManager().restartLoader(OPERATION_LOADER_ID, null, this);

                operationItemTouchHelper.attachToRecyclerView(recyclerView);
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
                        CategoryEntry.buildCategoryCostUri(year, month),
                        null,
                        null,
                        null,
                        CategoryEntry.TABLE_NAME +"."+CategoryEntry.COLUMN_TYPE + "," + CategoryEntry.TABLE_NAME +"."+CategoryEntry.COLUMN_TITLE);
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
        //TODO
        if (loader.getId() == ACCOUNT_LOADER_ID && mAdapter instanceof AccountAdapter
                || loader.getId() == CATEGORY_LOADER_ID && mAdapter instanceof CategoryAdapter
                || loader.getId() == OPERATION_LOADER_ID && mAdapter instanceof OperationAdapter)
            mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        animateOpenActivity();
    }

    private void animateOpenActivity(){
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
    }

}