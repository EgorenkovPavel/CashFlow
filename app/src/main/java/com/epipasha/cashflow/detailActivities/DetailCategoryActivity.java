package com.epipasha.cashflow.detailActivities;

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
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.epipasha.cashflow.NumberTextWatcherForThousand;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.objects.OperationType;

public class DetailCategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_DETAIL_LOADER = 353;

    private Uri mUri;
    private EditText etTitle, etBudget;
    private RadioGroup rgType;
    private boolean isNew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText)findViewById(R.id.category_detail_name);
        etBudget = (EditText)findViewById(R.id.category_detail_budget);
        rgType = (RadioGroup)findViewById(R.id.type_group);

        mUri = getIntent().getData();
        isNew = mUri == null;

        if (!isNew) {
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        String title = etTitle.getText().toString();

        int budget = 0;
        try {
            budget = Integer.valueOf(etBudget.getText().toString());
        }catch (Exception e){

        }

        //TODO Ошибка опредения бюжетаесли поле не заполнено, возможно нужно применить NumberTextWatcherForThousand
        OperationType type = getSelectedType();

        if(isNew && title.isEmpty()){
            return;
        }

        if (type == null){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_TITLE, title);
        values.put(CategoryEntry.COLUMN_TYPE, type.toDbValue());
        values.put(CategoryEntry.COLUMN_BUDGET, budget);

        if (isNew){
            mUri = getContentResolver().insert(CategoryEntry.CONTENT_URI, values);
            isNew = false;
        } else {
            getContentResolver().update(mUri, values, null, null);
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
            etTitle.setText(data.getString(data.getColumnIndex(CategoryEntry.COLUMN_TITLE)));
            etBudget.setText(String.valueOf(data.getInt(data.getColumnIndex(CategoryEntry.COLUMN_BUDGET))));
            setCheckedType(OperationType.toEnum(data.getInt(data.getColumnIndex(CategoryEntry.COLUMN_TYPE))));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private OperationType getSelectedType(){
        switch (rgType.getCheckedRadioButtonId()){
            case R.id.category_detail_in: return OperationType.IN;
            case R.id.category_detail_out: return OperationType.OUT;
            default: return null;
        }
    }

    private void setCheckedType(OperationType type){
        switch (type){
            case IN:{
                rgType.check(R.id.category_detail_in);
                break;
            }
            case OUT:{
                rgType.check(R.id.category_detail_out);
                break;
            }
        }
    }
}
