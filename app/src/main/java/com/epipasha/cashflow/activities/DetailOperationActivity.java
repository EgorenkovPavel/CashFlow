package com.epipasha.cashflow.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.epipasha.cashflow.objects.OperationType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DetailOperationActivity extends DetailActivity {

    public static final String EXTRA_OPERATION_ID = "extraOperationId";

    private static final int DEFAULT_OPERATION_ID = -1;

    private int mOperationId = DEFAULT_OPERATION_ID;

    private AppDatabase mDb;

    private Date operationDate;
    private Account operationAccount;
    private Category operationCategory;
    private Account operationRecipientAccount;
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

        accountSpinner = findViewById(R.id.operation_detail_account);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (getSelectedType(rgType.getCheckedRadioButtonId()) == OperationType.TRANSFER){
                    setAnalyticAdapter(OperationType.TRANSFER);
                }
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

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_OPERATION_ID)){
            setTitle(getString(R.string.operation));
            mOperationId = i.getIntExtra(EXTRA_OPERATION_ID, DEFAULT_OPERATION_ID);
            AppExecutors.getInstance().discIO().execute(new Runnable() {
                @Override
                public void run() {
                    final LiveData<OperationWithData> operation = mDb.operationDao().loadOperationWithDataById(mOperationId);

                    operation.observe(DetailOperationActivity.this, new Observer<OperationWithData>() {
                        @Override
                        public void onChanged(@Nullable OperationWithData operation) {
                            populateUI(operation);
                        }
                    });
                }
            });
        }else {
            setTitle(getString(R.string.new_operation));
            initAccountSpinner();
            setSelectedType(OperationType.IN);
        }
    }

    private void initAccountSpinner(){
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                final LiveData<List<Account>> accounts = mDb.accountDao().loadAllAccounts();

                accounts.observe(DetailOperationActivity.this, new Observer<List<Account>>() {
                    @Override
                    public void onChanged(@Nullable List<Account> accounts) {
                        ArrayAdapter<Account> adapter = new ArrayAdapter<>(DetailOperationActivity.this,
                                android.R.layout.simple_spinner_item,
                                android.R.id.text1,
                                accounts);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        accountSpinner.setAdapter(adapter);
                        accountSpinner.setSelection(accounts.indexOf(operationAccount));
                    }
                });
            }
        });
    }

    private void populateUI(OperationWithData operationWithData) {

        if (operationWithData == null) {
            return;
        }

        operationDate = operationWithData.getDate();
        setSelectedDate();
        edtSum.setText(String.valueOf(operationWithData.getSum()));

        operationAccount = operationWithData.getAccount();
        operationCategory = operationWithData.getCategory();
        operationRecipientAccount = operationWithData.getRepAccount();

        initAccountSpinner();

        setSelectedType(operationWithData.getType());
    }

    private void setAnalyticAdapter(final OperationType type){
        switch (type){
            case IN: case OUT:{
                AppExecutors.getInstance().discIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        LiveData<List<Category>> categories = mDb.categoryDao().loadAllCategoriesByType(type);

                           categories.observe(DetailOperationActivity.this, new Observer<List<Category>>() {
                            @Override
                            public void onChanged(@Nullable List<Category> categories) {
                                ArrayAdapter<Category> adapter = new ArrayAdapter<>(DetailOperationActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        android.R.id.text1,
                                        categories);

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                analyticSpinner.setAdapter(adapter);
                                analyticSpinner.setSelection(categories.indexOf(operationCategory));
                            }
                        });

                      }
                });
                break;
            }
            case TRANSFER: {
                AppExecutors.getInstance().discIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        LiveData<List<Account>> accounts = null;
                        operationAccount = (Account) accountSpinner.getSelectedItem();
                        if (operationAccount == null)
                            accounts = mDb.accountDao().loadAllAccounts();
                        else
                            accounts = mDb.accountDao().loadAllAccountsExceptId(operationAccount.getId());

                        accounts.observe(DetailOperationActivity.this, new Observer<List<Account>>() {
                            @Override
                            public void onChanged(@Nullable List<Account> accounts) {

                                ArrayAdapter<Account> adapter = new ArrayAdapter<>(DetailOperationActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        android.R.id.text1,
                                        accounts);

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                analyticSpinner.setAdapter(adapter);
                                analyticSpinner.setSelection(accounts.indexOf(operationRecipientAccount));
                            }
                        });
                     }
                });
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

        operationAccount = (Account) accountSpinner.getSelectedItem();

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
                operationCategory = (Category) analyticSpinner.getSelectedItem();
                categoryId = operationCategory == null ? 0 : operationCategory.getId();
                if (categoryId <= 0){
                    Toast.makeText(this, getString(R.string.error_choose_category), Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            }
            case TRANSFER:{
                operationRecipientAccount = (Account) analyticSpinner.getSelectedItem();
                recAccountId = operationRecipientAccount == null ? 0 : operationRecipientAccount.getId();
                if(recAccountId <= 0){
                    Toast.makeText(this, getString(R.string.error_choose_rep_account), Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            }
        }

        final Operation operation = new Operation(operationDate, type, accountId, categoryId, recAccountId, sum);
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mOperationId == DEFAULT_OPERATION_ID){
                    mDb.operationDao().insertOperationWithAnalytic(operation);
                }else{
                    operation.setId(mOperationId);
                    mDb.operationDao().updateOperationWithAnalytic(operation);
                }
                finish();
            }
        });
    }

}
