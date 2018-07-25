package com.epipasha.cashflow.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.epipasha.cashflow.NumberTextWatcherForThousand;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.data.entites.OperationWithData;
import com.epipasha.cashflow.data.viewmodel.ModelFactory;
import com.epipasha.cashflow.data.viewmodel.OperationDetailViewModel;
import com.epipasha.cashflow.objects.OperationType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DetailOperationActivity extends DetailActivity {

    public static final String EXTRA_OPERATION_ID = "extraOperationId";

    private static final int DEFAULT_OPERATION_ID = -1;

    private int mOperationId = DEFAULT_OPERATION_ID;

    private OperationDetailViewModel model;
    private Adapter<Account> mAccountAdapter;
    private Adapter<Category> mCategoryInAdapter;
    private Adapter<Category> mCategoryOutAdapter;
    private Adapter<Account> mRecAccountAdapter;

    private Date operationDate;
    private NumberTextWatcherForThousand sumWatcher;

    private Spinner analyticSpinner;
    private Spinner accountSpinner;
    private TextView edtDate, edtTime;
    private EditText edtSum;
    private RadioGroup rgType;
    private TextView lblAccount, lblAnalytic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_operation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtDate = findViewById(R.id.operation_detail_date);
        edtTime = findViewById(R.id.operation_detail_time);
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTime();
            }
        });

        rgType = findViewById(R.id.operation_detail_type_group);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                 onOperationTypeChanged(getSelectedType(i));
            }
        });

        lblAccount = findViewById(R.id.operation_detail_label_account);
        lblAnalytic = findViewById(R.id.operation_detail_label_category);

        createAdapters();

        accountSpinner = findViewById(R.id.operation_detail_account);
        accountSpinner.setAdapter(mAccountAdapter);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                model.setSelectedAccount(mAccountAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        analyticSpinner = findViewById(R.id.operation_detail_category);

        edtSum = findViewById(R.id.operation_detail_sum);
        sumWatcher = new NumberTextWatcherForThousand(edtSum);
        edtSum.addTextChangedListener(sumWatcher);

        operationDate = Calendar.getInstance().getTime();
        setSelectedDate();

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_OPERATION_ID)){
            setTitle(getString(R.string.operation));
            mOperationId = i.getIntExtra(EXTRA_OPERATION_ID, DEFAULT_OPERATION_ID);
          }else {
            setTitle(getString(R.string.new_operation));
            setSelectedType(OperationType.IN);
        }

        model = ViewModelProviders.of(this, new ModelFactory(getApplication(), mOperationId)).get(OperationDetailViewModel.class);

        model.getAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> accounts) {
                mAccountAdapter.addAll(accounts);
            }
        });

        model.getCategoriesIn().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                mCategoryInAdapter.addAll(categories);
            }
        });

        model.getCategoriesOut().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                mCategoryOutAdapter.addAll(categories);
            }
        });

        model.getRecAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> accounts) {
                mRecAccountAdapter.addAll(accounts);
            }
        });

        model.getOperationWithData().observe(this, new Observer<OperationWithData>() {
            @Override
            public void onChanged(@Nullable OperationWithData operationWithData) {
                populateUI(operationWithData);
            }
        });
    }

    private void createAdapters(){
        mAccountAdapter = new Adapter<>(this);
        mCategoryInAdapter = new Adapter<>(this);
        mCategoryOutAdapter = new Adapter<>(this);
        mRecAccountAdapter = new Adapter<>(this);
    }

    private void populateUI(OperationWithData operationWithData) {

        if (operationWithData == null) {
            return;
        }

        operationDate = operationWithData.getDate();
        setSelectedDate();
        edtSum.setText(String.valueOf(operationWithData.getSum()));

        Account operationAccount = operationWithData.getAccount();
        Category operationCategory = operationWithData.getCategory();
        Account operationRecipientAccount = operationWithData.getRepAccount();

        setSelectedType(operationWithData.getType());

        //todo fix seting current value - loop
        accountSpinner.setSelection(((Adapter)accountSpinner.getAdapter()).getPositionById(operationAccount.getId()));

        switch (operationWithData.getType()){
            case IN:case OUT: {
                analyticSpinner.setSelection(((Adapter) analyticSpinner.getAdapter()).getPositionById(operationCategory.getId()));
                break;
            }
            case TRANSFER:{
                analyticSpinner.setSelection(((Adapter) analyticSpinner.getAdapter()).getPositionById(operationRecipientAccount.getId()));
                break;
            }
        }
    }

    private void setAnalyticAdapter(final OperationType type){
        switch (type){
            case IN: {
                analyticSpinner.setAdapter(mCategoryInAdapter);
                break;
            }case OUT:{
                analyticSpinner.setAdapter(mCategoryOutAdapter);
                break;
            }case TRANSFER: {
                analyticSpinner.setAdapter(mRecAccountAdapter);
                break;
            }
        }
    }

    private void setSelectedDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        edtDate.setText(format.format(operationDate));

        format.applyPattern("HH:mm");
        edtTime.setText(format.format(operationDate));
    }

    private void setSelectedType(OperationType type){
        switch (type){
            case IN: {
                rgType.check(R.id.operation_detail_btnIn);
                break;
            }
            case OUT: {
                rgType.check(R.id.operation_detail_btnOut);
               break;
            }
            case TRANSFER: {
                rgType.check(R.id.operation_detail_btnTransfer);
                break;
            }
        }
    }

    private void onOperationTypeChanged(final OperationType type){
        setAnalyticAdapter(type);

        if(type == OperationType.TRANSFER){
            lblAccount.setText(getString(R.string.from));
            lblAnalytic.setText(getString(R.string.to));
        }else{
            lblAccount.setText(getString(R.string.account));
            lblAnalytic.setText(getString(R.string.category));
        }

    }

    private OperationType getSelectedType(int id){
        switch (id){
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

                        setSelectedDate();
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

    @Override
    public void saveObject(){
        int sum = (int) sumWatcher.getLong(edtSum.getText().toString());

        if(sum==0){
            edtSum.setError(getString(R.string.error_fill_sum));
            return;
        }

        Account operationAccount = (Account) accountSpinner.getSelectedItem();

        int accountId = operationAccount == null ? 0 : operationAccount.getId();
        if(accountId <=0){
            Toast.makeText(this, getString(R.string.error_choose_account), Toast.LENGTH_SHORT).show();
            return;
        }

        Integer categoryId = null;
        Integer recAccountId = null;

        OperationType type = getSelectedType(rgType.getCheckedRadioButtonId());
        switch (type){
            case IN: case OUT:{
                Category operationCategory = (Category) analyticSpinner.getSelectedItem();
                categoryId = operationCategory == null ? -1 : operationCategory.getId();
                if (categoryId <= 0){
                    Toast.makeText(this, getString(R.string.error_choose_category), Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            }
            case TRANSFER:{
                Account operationRecipientAccount = (Account) analyticSpinner.getSelectedItem();
                recAccountId = operationRecipientAccount == null ? -1 : operationRecipientAccount.getId();
                if(recAccountId <= 0){
                    Toast.makeText(this, getString(R.string.error_choose_rep_account), Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            }
        }

        final Operation operation = new Operation(operationDate, type, accountId, categoryId, recAccountId, sum);
        model.saveObject(operation);
        finish();
    }

    private class Adapter<T> extends ArrayAdapter<T>{

        public Adapter(@NonNull Context context) {
            super(context,
                    android.R.layout.simple_spinner_item,
                    android.R.id.text1);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        public int getPositionById(int id){

            int i = 0;
            for (i=0; i<getCount();i++){
                T object = getItem(i);

                if(object instanceof Account && ((Account) object).getId() == id){
                    break;
                }else if(object instanceof Category && ((Category) object).getId() == id) {
                    break;
                }
            }
            return i;
        }

        @Override
        public void addAll(@NonNull Collection<? extends T> collection) {
            super.clear();
            super.addAll(collection);
            super.notifyDataSetChanged();
        }
    }

}
