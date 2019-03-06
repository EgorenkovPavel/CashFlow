package com.epipasha.cashflow.operations;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.ActivityOperationBinding;
import com.epipasha.cashflow.activities.DetailActivity;
import com.epipasha.cashflow.data.ViewModelFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class OperationActivity extends DetailActivity {

    public static final String EXTRA_OPERATION_ID = "extraOperationId";

    private static final int DEFAULT_OPERATION_ID = -1;

    private OperationViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityOperationBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_operation);

        setSupportActionBar(binding.toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this,
                ViewModelFactory.getInstance(getApplication()))
                .get(OperationViewModel.class);

        binding.setViewmodel(model);

        TextView edtDate = binding.operationDetailDate;
        TextView edtTime = binding.operationDetailTime;
        edtDate.setOnClickListener(view -> chooseDate());
        edtTime.setOnClickListener(view -> chooseTime());

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_OPERATION_ID)){
            int mOperationId = i.getIntExtra(EXTRA_OPERATION_ID, DEFAULT_OPERATION_ID);
            model.start(mOperationId);
        }

        model.getStatus().observe(this, status -> {
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
        });
    }

    private void chooseDate(){

        final Date operationDate = model.getOperationDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(operationDate);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (datePicker, year, monthOfYear, dayOfMonth) -> {
                    Calendar c = new GregorianCalendar();
                    c.setTime(operationDate);
                    c.set(year, monthOfYear, dayOfMonth);
                    model.setOperationDate(new Date(c.getTimeInMillis()));
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
                (timePicker, hours, minutes) -> {
                    Calendar c = new GregorianCalendar();
                    c.setTime(operationDate);
                    c.set(Calendar.HOUR_OF_DAY, hours);
                    c.set(Calendar.MINUTE, minutes);
                    model.setOperationDate(new Date(c.getTimeInMillis()));
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
