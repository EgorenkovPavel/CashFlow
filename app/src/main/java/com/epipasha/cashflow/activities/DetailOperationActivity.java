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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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

    private NumberTextWatcherForThousand sumWatcher;

    private Spinner analyticSpinner;
    private Spinner accountSpinner;
    private TextView edtDate, edtTime;
    private EditText edtSum;
    private RadioGroup rgType;
    private TextView lblAccount, lblAnalytic;
    private RadioButton rbIn, rbOut, rbTransfer;

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
                switch (i){
                    case R.id.operation_detail_btnIn: {
                        model.setOperationType(OperationType.IN);
                        break;
                    }
                    case R.id.operation_detail_btnOut: {
                        model.setOperationType(OperationType.OUT);
                        break;
                    }
                    case R.id.operation_detail_btnTransfer: {
                        model.setOperationType(OperationType.TRANSFER);
                        break;
                    }
                }
            }
        });

        rbIn = findViewById(R.id.operation_detail_btnIn);
        rbOut = findViewById(R.id.operation_detail_btnOut);
        rbTransfer = findViewById(R.id.operation_detail_btnTransfer);

        lblAccount = findViewById(R.id.operation_detail_label_account);
        lblAnalytic = findViewById(R.id.operation_detail_label_category);

        createAdapters();

        accountSpinner = findViewById(R.id.operation_detail_account);
        accountSpinner.setAdapter(mAccountAdapter);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                model.setOperationAccount(mAccountAdapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        analyticSpinner = findViewById(R.id.operation_detail_category);
        analyticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Adapter adapter = (Adapter) analyticSpinner.getAdapter();
                if(adapter.equals(mCategoryInAdapter))
                    model.setOperationCategory(mCategoryInAdapter.getItem(position));
                else if(adapter.equals(mCategoryOutAdapter))
                    model.setOperationCategory(mCategoryOutAdapter.getItem(position));
                else if(adapter.equals(mRecAccountAdapter))
                    model.setOperationRecAccount(mRecAccountAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtSum = findViewById(R.id.operation_detail_sum);
        sumWatcher = new sumWather(edtSum);
        edtSum.addTextChangedListener(sumWatcher);


        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_OPERATION_ID)){
            setTitle(getString(R.string.operation));
            mOperationId = i.getIntExtra(EXTRA_OPERATION_ID, DEFAULT_OPERATION_ID);
          }else {
            setTitle(getString(R.string.new_operation));
        }

        model = ViewModelProviders.of(this, new ModelFactory(getApplication(), mOperationId)).get(OperationDetailViewModel.class);

        model.getAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> accounts) {
                if (accounts == null) return;
                mAccountAdapter.addAll(accounts);
                setAccount(model.getOperationAccountId().getValue());
            }
        });

        model.getCategoriesIn().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                if(categories == null) return;
                mCategoryOutAdapter.addAll(categories);
                setCategory(model.getOperationCategoryId().getValue());
            }
        });

        model.getCategoriesOut().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                if(categories == null) return;
                mCategoryOutAdapter.addAll(categories);
                setCategory(model.getOperationCategoryId().getValue());
            }
        });

        model.getRecAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> accounts) {
                if(accounts == null) return;
                mRecAccountAdapter.addAll(accounts);
                setRecAccount(model.getOperationRecAccountId().getValue());
            }
        });

        model.getOperationWithData().observe(this, new Observer<OperationWithData>() {
            @Override
            public void onChanged(@Nullable OperationWithData operationWithData) {
                populateUI(operationWithData);
            }
        });

        model.getOperationAccountId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                setAccount(id);
            }
        });

        model.getOperationCategoryId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                setCategory(id);
            }
        });

        model.getOperationRecAccountId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                setRecAccount(id);
            }
        });

        model.getOperationDate().observe(this, new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date date) {
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                edtDate.setText(format.format(date));

                format.applyPattern("HH:mm");
                edtTime.setText(format.format(date));
            }
        });

        model.getOperationType().observe(this, new Observer<OperationType>() {
            @Override
            public void onChanged(@Nullable OperationType type) {
                if(type == null) return;

                rbIn.setChecked(type == OperationType.IN);
                rbOut.setChecked(type == OperationType.OUT);
                rbTransfer.setChecked(type == OperationType.TRANSFER);

                setAnalyticAdapter(type);

                if(type == OperationType.TRANSFER){
                    lblAccount.setText(getString(R.string.from));
                    lblAnalytic.setText(getString(R.string.to));
                }else{
                    lblAccount.setText(getString(R.string.account));
                    lblAnalytic.setText(getString(R.string.category));
                }
            }
        });

        model.getOperationSum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer sum) {
                edtSum.setText(String.valueOf(sum));
            }
        });

        model.getStatus().observe(this, new Observer<OperationDetailViewModel.Status>() {
            @Override
            public void onChanged(@Nullable OperationDetailViewModel.Status status) {
                if(status == null) return;

                switch (status){
                    case EMPTY_SUM: {
                        edtSum.setError(getString(R.string.error_fill_sum));
                        break;
                    }
                    case EMPTY_ACCOUNT:{
                        Toast.makeText(DetailOperationActivity.this,
                                getString(R.string.error_choose_account),
                                Toast.LENGTH_SHORT)
                                .show();
                        break;
                    }
                    case EMPTY_ANALYTIC:{
                        Toast.makeText(DetailOperationActivity.this,
                                getString(R.string.no_analytic_selected),
                                Toast.LENGTH_SHORT)
                                .show();
                        break;
                    }
                    case OPERATION_SAVED:{
                        finish();
                        break;
                    }

                }
            }
        });
    }

    private void createAdapters(){
        mAccountAdapter = new Adapter<>(this);
        mCategoryInAdapter = new Adapter<>(this);
        mCategoryOutAdapter = new Adapter<>(this);
        mRecAccountAdapter = new Adapter<>(this);
    }

    private void setAccount(Integer id){
        if(id == null) return;
        accountSpinner.setSelection(((Adapter)accountSpinner.getAdapter()).getPositionById(id));
    }

    private void setCategory(Integer id){
        if(id == null) return;
        Adapter adapter = (Adapter) analyticSpinner.getAdapter();
        if(adapter.equals(mCategoryInAdapter)||adapter.equals(mCategoryOutAdapter))
            analyticSpinner.setSelection(((Adapter) analyticSpinner.getAdapter()).getPositionById(id));
    }

    private void setRecAccount(Integer id){
        if(id == null) return;
        Adapter adapter = (Adapter) analyticSpinner.getAdapter();
        if(adapter.equals(mRecAccountAdapter))
            analyticSpinner.setSelection(((Adapter) analyticSpinner.getAdapter()).getPositionById(id));
    }

    private void populateUI(OperationWithData operationWithData) {

        if (operationWithData == null) {
            return;
        }

        model.setOperationDate(operationWithData.getDate());
        model.setOperationType(operationWithData.getType());
        model.setOperationAccount(operationWithData.getAccount());
        model.setOperationCategory(operationWithData.getCategory());
        model.setOperationRecAccount(operationWithData.getRepAccount());
        model.setOperationSum(operationWithData.getSum());
    }

    private void setAnalyticAdapter(final OperationType type){
        switch (type){
            case IN: {
                analyticSpinner.setAdapter(mCategoryInAdapter);
                setCategory(model.getOperationCategoryId().getValue());
                break;
            }case OUT:{
                analyticSpinner.setAdapter(mCategoryOutAdapter);
                setCategory(model.getOperationCategoryId().getValue());
                break;
            }case TRANSFER: {
                analyticSpinner.setAdapter(mRecAccountAdapter);
                setRecAccount(model.getOperationRecAccountId().getValue());
                break;
            }
        }
    }

    private void chooseDate(){

        final Date operationDate = model.getOperationDate().getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(operationDate);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c = new GregorianCalendar();
                        c.setTime(operationDate);
                        c.set(year, monthOfYear, dayOfMonth);
                        model.setOperationDate(new Date(c.getTimeInMillis()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void chooseTime(){
        final Date operationDate = model.getOperationDate().getValue();
        Calendar calendar = Calendar.getInstance();
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
                        model.setOperationDate(new Date(c.getTimeInMillis()));
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        dialog.show();
    }

    @Override
    public void saveObject(){
        model.saveObject();
    }

    private class sumWather extends NumberTextWatcherForThousand{

        public sumWather(EditText editText) {
            super(editText);
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            model.setOperationSum((int)this.getLong(s.toString()));
        }
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
