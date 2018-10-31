package com.epipasha.cashflow.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.viewmodel.OperationViewModel;
import com.epipasha.cashflow.viewmodel.ViewModelFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OperationActivity extends DetailActivity {

    public static final String EXTRA_OPERATION_ID = "extraOperationId";

    private static final int DEFAULT_OPERATION_ID = -1;

    private OperationViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityOperationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_operation);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication())).get(OperationViewModel.class);

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

        model.getStatus().observe(this, new Observer<OperationViewModel.Status>() {
            @Override
            public void onChanged(@Nullable OperationViewModel.Status status) {
                if(status == null) return;

                switch (status){
                    case EMPTY_SUM: {
                        binding.operationDetailSum.setError(getString(R.string.error_fill_sum));
                        break;
                    }
                    case EMPTY_ACCOUNT:{
                        Toast.makeText(OperationActivity.this,
                                getString(R.string.error_choose_account),
                                Toast.LENGTH_SHORT)
                                .show();
                        break;
                    }
                    case EMPTY_ANALYTIC:{
                        Toast.makeText(OperationActivity.this,
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
