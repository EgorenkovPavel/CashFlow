package com.epipasha.cashflow.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.epipasha.cashflow.NumberTextWatcherForThousand;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.OperationWithData;
import com.epipasha.cashflow.data.viewmodel.ViewModelFactory;
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

    private OperationDetailViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityDetailOperationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_operation);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication())).get(OperationDetailViewModel.class);

        binding.setViewmodel(model);

        TextView edtDate = binding.operationDetailDate;
        TextView edtTime = binding.operationDetailTime;
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

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_OPERATION_ID)){
            int mOperationId = i.getIntExtra(EXTRA_OPERATION_ID, DEFAULT_OPERATION_ID);
            model.start(mOperationId);
        }

        model.getStatus().observe(this, new Observer<OperationDetailViewModel.Status>() {
            @Override
            public void onChanged(@Nullable OperationDetailViewModel.Status status) {
                if(status == null) return;

                switch (status){
                    case EMPTY_SUM: {
                        binding.operationDetailSum.setError(getString(R.string.error_fill_sum));
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


    private void chooseDate(){

        final Date operationDate = model.getOperationDate();
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
        final Date operationDate = model.getOperationDate();
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

}
