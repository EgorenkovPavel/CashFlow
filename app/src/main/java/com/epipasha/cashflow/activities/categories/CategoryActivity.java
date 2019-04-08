package com.epipasha.cashflow.activities.categories;

import android.content.Intent;
import android.os.Bundle;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.activities.DetailActivity;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.data.dao.AnalyticDao.MonthCashflow;
import com.epipasha.cashflow.databinding.ActivityCategoryBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

public class CategoryActivity extends DetailActivity {

    private static final String EXTRA_CATEGORY_ID = "extraCategoryId";

    private CategoryViewModel model;

    private BarChart mChart;

    public static void start(FragmentActivity parentActivity, int id){
        Intent intent = new Intent(parentActivity, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_ID, id);
        parentActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCategoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_category);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication()))
                .get(CategoryViewModel.class);

        binding.setViewmodel(model);

        mChart = findViewById(R.id.chart);

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_CATEGORY_ID)){
            int mCategoryId = i.getIntExtra(EXTRA_CATEGORY_ID, Utils.EMPTY_ID);
            model.start(mCategoryId);
        }else{
            model.start();
        }

        model.getMonthCashflow().observe(this, monthCashflows -> {
            if(monthCashflows == null) return;
            loadChart(monthCashflows);
        });
    }

    @Override
    public void saveObject(){
        model.saveObject();
    }

    private void loadChart(List<MonthCashflow> monthCashflows){

        List<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        int column = 1;
        for (MonthCashflow monthCashflow:monthCashflows) {
            entries.add(new BarEntry(column, monthCashflow.getSum()));

            Calendar cal = Calendar.getInstance();
            cal.set(monthCashflow.getYear(), monthCashflow.getMonth(), 1);

            DateFormat df = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

            labels.add(df.format(cal.getTime()));

            column++;
        }

        //TODO add empty sum for date that don't exists in db

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColor(getResources().getColor(R.color.colorPrimary));

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        data.setValueTextSize(10);
        data.setHighlightEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter((value, axis) -> {
            if(value - (int)(value) == 0){
                return labels.get((int)value - 1);
            }else{
                return "";
            }
        });

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setAxisMinimum(0);

        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        mChart.setData(data);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.setVisibleXRangeMaximum(3);
        mChart.moveViewToX(column);
        mChart.invalidate(); // refresh
    }

}
