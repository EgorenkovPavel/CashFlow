package com.epipasha.cashflow.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;

public class DetailAccountActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_DETAIL_LOADER = 353;

    private Uri mUri;
    private EditText etTitle;
    private boolean isNew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText)findViewById(R.id.account_detail_name);

        mUri = getIntent().getData();
        isNew = mUri == null;

        if (!isNew) {
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
        }

        if(isNew) {
            setTitle(getString(R.string.new_account));
        }else{
            setTitle(getString(R.string.account));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:{
                saveAccount();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            etTitle.setText(data.getString(data.getColumnIndex(AccountEntry.COLUMN_TITLE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void saveAccount(){
        String title = etTitle.getText().toString();

        if(title.isEmpty()){
            etTitle.setError(getString(R.string.error_fill_title));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AccountEntry.COLUMN_TITLE, title);

        if (isNew){
            mUri = getContentResolver().insert(AccountEntry.CONTENT_URI, values);
            isNew = false;
        } else {
            getContentResolver().update(mUri, values, null, null);
        }

        finish();
    }
}