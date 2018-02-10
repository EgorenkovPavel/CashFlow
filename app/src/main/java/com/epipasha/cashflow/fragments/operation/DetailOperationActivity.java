package com.epipasha.cashflow.fragments.operation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.epipasha.cashflow.NumberTextWatcherForThousand;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.data.CashFlowContract.OperationEntry;
import com.epipasha.cashflow.objects.OperationType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DetailOperationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_OPERATION_LOADER = 353;
    private static final int ID_ACCOUNT_LOADER = 438;
    private static final int ID_CATEGORY_LOADER = 543;
    private static final int ID_RECIPIENT_ACCOUNT_LOADER = 879;

    private Uri mUri;
    private boolean isNew;

    private Date operationDate;
    private int accountId, categoryId, recAccountId;
    private NumberTextWatcherForThousand sumWatcher;

    private Spinner categorySpinner;
    private Spinner accountSpinner;
    private Spinner recipientAccountSpinner;
    private TextView edtDate;
    private EditText edtSum;
    private RadioGroup rgType;
    private TextView lblAccount,lblCategory, lblRecipientAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_operation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtDate = (TextView)findViewById(R.id.operation_detail_date);
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });
        rgType = (RadioGroup) findViewById(R.id.operation_detail_type_group);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setSelectedType(getSelectedType());
            }
        });

        lblAccount = (TextView) findViewById(R.id.operation_detail_label_account);
        lblCategory = (TextView) findViewById(R.id.operation_detail_label_category);
        lblRecipientAccount = (TextView) findViewById(R.id.operation_detail_label_recipient_account);

        accountSpinner = (Spinner) findViewById(R.id.operation_detail_account);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (getSelectedType() == OperationType.TRANSFER){
                    getSupportLoaderManager().restartLoader(ID_RECIPIENT_ACCOUNT_LOADER, null, DetailOperationActivity.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        categorySpinner = (Spinner) findViewById(R.id.operation_detail_category);
        recipientAccountSpinner = (Spinner) findViewById(R.id.operation_detail_recipient_account);

        edtSum = (EditText) findViewById(R.id.operation_detail_sum);
        sumWatcher = new NumberTextWatcherForThousand(edtSum);
        edtSum.addTextChangedListener(sumWatcher);

        mUri = getIntent().getData();
        isNew = mUri == null;

        if (!isNew) {
            getSupportLoaderManager().initLoader(ID_OPERATION_LOADER, null, this);
        }else{
            operationDate = new Date();
            setSelectedDate();
            setSelectedType(OperationType.IN);
            getSupportLoaderManager().initLoader(ID_ACCOUNT_LOADER, null, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        int sum = (int) sumWatcher.getLong(edtSum.getText().toString());

        if(isNew && sum==0){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(OperationEntry.COLUMN_DATE, operationDate.getTime());
        values.put(OperationEntry.COLUMN_SUM, sum);
        values.put(OperationEntry.COLUMN_ACCOUNT_ID, Utils.getSelectedId(accountSpinner));

        OperationType type = getSelectedType();
        values.put(OperationEntry.COLUMN_TYPE, getSelectedType().toDbValue());
        switch (type){
            case IN: case OUT:{
                values.put(OperationEntry.COLUMN_CATEGORY_ID, Utils.getSelectedId(categorySpinner));
                break;
            }
            case TRANSFER:{
                values.put(OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID, Utils.getSelectedId(recipientAccountSpinner));
                break;
            }
        }

        if (isNew){
            mUri = getContentResolver().insert(OperationEntry.CONTENT_URI, values);
            isNew = false;
        } else {
            getContentResolver().update(mUri, values, null, null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {

            case ID_OPERATION_LOADER:
                return new CursorLoader(this,
                        mUri,
                        null,
                        null,
                        null,
                        null);

            case ID_ACCOUNT_LOADER:
                return new CursorLoader(this,
                        AccountEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            case ID_CATEGORY_LOADER:
                return new CursorLoader(this,
                        CategoryEntry.CONTENT_URI,
                        null,
                        CategoryEntry.COLUMN_TYPE + " = ?",
                        new String[]{String.valueOf(getSelectedType().toDbValue())},
                        null);

            case ID_RECIPIENT_ACCOUNT_LOADER:
                return new CursorLoader(this,
                        AccountEntry.CONTENT_URI,
                        null,
                        AccountEntry._ID + " <> ?",
                        new String[]{String.valueOf(Utils.getSelectedId(accountSpinner))},
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()){
            case ID_OPERATION_LOADER:{
                if(data != null && data.moveToFirst()){

                    operationDate = new Date(data.getLong(data.getColumnIndex(OperationEntry.COLUMN_DATE)));
                    setSelectedDate();
                    edtSum.setText(String.valueOf(data.getInt(data.getColumnIndex(OperationEntry.COLUMN_SUM))));

                    OperationType type = OperationType.toEnum(data.getInt(data.getColumnIndex(OperationEntry.COLUMN_TYPE)));
                    setSelectedType(type);

                    accountId = data.getInt(data.getColumnIndex(OperationEntry.COLUMN_ACCOUNT_ID));
                    categoryId = data.getInt(data.getColumnIndex(OperationEntry.COLUMN_CATEGORY_ID));
                    recAccountId = data.getInt(data.getColumnIndex(OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID));

                    getSupportLoaderManager().initLoader(ID_ACCOUNT_LOADER, null, this);
                }

                break;
            }
            case ID_ACCOUNT_LOADER:{
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        data,
                        new String[]{AccountEntry.COLUMN_TITLE},
                        new int[]{android.R.id.text1}
                        ,0);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                accountSpinner.setAdapter(adapter);

                Utils.setPositionById(accountSpinner, accountId);
                break;
            }
            case ID_CATEGORY_LOADER:{
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        data,
                        new String[]{CategoryEntry.COLUMN_TITLE},
                        new int[]{android.R.id.text1}
                        ,0);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);

                Utils.setPositionById(categorySpinner, categoryId);
                break;
            }

            case ID_RECIPIENT_ACCOUNT_LOADER:{
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        data,
                        new String[]{AccountEntry.COLUMN_TITLE},
                        new int[]{android.R.id.text1}
                        ,0);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                recipientAccountSpinner.setAdapter(adapter);

                Utils.setPositionById(recipientAccountSpinner, recAccountId);
                break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setSelectedDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        edtDate.setText(format.format(operationDate));
    }

    private void setSelectedType(OperationType type){
        switch (type){
            case IN: {
                rgType.check(R.id.operation_detail_btnIn);
                getSupportLoaderManager().restartLoader(ID_CATEGORY_LOADER, null, this);
                break;
            }
            case OUT: {
                rgType.check(R.id.operation_detail_btnOut);
                getSupportLoaderManager().restartLoader(ID_CATEGORY_LOADER, null, this);
                break;
            }
            case TRANSFER: {
                rgType.check(R.id.operation_detail_btnTransfer);
                getSupportLoaderManager().restartLoader(ID_RECIPIENT_ACCOUNT_LOADER, null, this);
                break;
            }
        }

        int categoryVis = type ==OperationType.TRANSFER ? View.GONE : View.VISIBLE;
        int repAccountVis = type !=OperationType.TRANSFER  ? View.GONE : View.VISIBLE;

        lblCategory.setVisibility(categoryVis);
        categorySpinner.setVisibility(categoryVis);

        lblRecipientAccount.setVisibility(repAccountVis);
        recipientAccountSpinner.setVisibility(repAccountVis);

        lblAccount.setText(type ==OperationType.TRANSFER ? getResources().getText(R.string.from) : getResources().getText(R.string.account));

    }

    private OperationType getSelectedType(){
        switch (rgType.getCheckedRadioButtonId()){
            case R.id.operation_detail_btnIn: return OperationType.IN;
            case R.id.operation_detail_btnOut: return OperationType.OUT;
            case R.id.operation_detail_btnTransfer: return OperationType.TRANSFER;
            default: return null;
        }
    }

    private void chooseDate(){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(operationDate);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c = new GregorianCalendar();
                        c.setTime(operationDate);
                        c.set(year, monthOfYear, dayOfMonth);
                        operationDate = new Date(c.getTimeInMillis());

                        chooseTime();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void chooseTime(){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(operationDate);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        Calendar c = new GregorianCalendar();
                        c.setTime(operationDate);
                        c.set(Calendar.HOUR_OF_DAY, hours);
                        c.set(Calendar.MINUTE, minutes);
                        operationDate = new Date(c.getTimeInMillis());

                        setSelectedDate();
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

}
