package com.epipasha.cashflow.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.dao.AnalyticDao.MonthCashflow;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.viewmodel.AccountDetailViewModel;
import com.epipasha.cashflow.data.viewmodel.CategoryDetailViewModel;
import com.epipasha.cashflow.data.viewmodel.ViewModelFactory;
import com.epipasha.cashflow.databinding.ActivityDetailCategoryBinding;
import com.epipasha.cashflow.objects.OperationType;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailCategoryActivity extends DetailActivity{

    public static final String EXTRA_CATEGORY_ID = "extraCategoryId";

    private static final int DEFAULT_CATEGORY_ID = -1;
    private CategoryDetailViewModel model;

    private EditText etTitle, etBudget;
    private RadioGroup rgType;
    private BarChart mChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityDetailCategoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_category);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication()))
                .get(CategoryDetailViewModel.class);

        binding.setViewmodel(model);

        mChart = findViewById(R.id.chart);

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_CATEGORY_ID)){
            int mCategoryId = i.getIntExtra(EXTRA_CATEGORY_ID, DEFAULT_CATEGORY_ID);
            model.start(mCategoryId);
        }
    }

    @Override
    public void saveObject(){
        model.saveObject();
        finish();

    }

    private void loadChart(List<MonthCashflow> monthCashflows){

        List<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        int column = monthCashflows.size();
        for (MonthCashflow monthCashflow:monthCashflows) {
            entries.add(new BarEntry(column, monthCashflow.getSum()));

            Calendar cal = Calendar.getInstance();
            cal.set(monthCashflow.getYear(), monthCashflow.getMonth(), 1);

            DateFormat df = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

            labels.add(df.format(cal.getTime()));
        }

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColor(getResources().getColor(R.color.colorPrimary));

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        data.setValueTextSize(10);
        data.setHighlightEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value - (int)(value) == 0){
                    return labels.get((int)value - 1);
                }else{
                    return "";
                }
            }
        });

        etBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addBudgetLineToChart();
            }
        });

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        addBudgetLineToChart();

        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        mChart.setData(data);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.setVisibleXRangeMaximum(3);
        mChart.moveViewToX(column);
        mChart.invalidate(); // refresh
    }

    private void addBudgetLineToChart(){
        mChart.getAxisLeft().getLimitLines().clear();

        String budgetText = etBudget.getText().toString();
        if(budgetText.isEmpty()){
            return;
        }

        float budget = Float.valueOf(budgetText);
        if(budget == 0){
            return;
        }

        YAxis yAxis = mChart.getAxisLeft();
        LimitLine line = new LimitLine(budget);
        line.setEnabled(true);
        line.setLineWidth(3);
        line.setLineColor(getResources().getColor(R.color.colorAccent));
        line.setTextColor(getResources().getColor(R.color.colorAccent));
        line.setLabel(getString(R.string.budget));
        yAxis.addLimitLine(line);

    }

}
