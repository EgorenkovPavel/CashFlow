package com.epipasha.cashflow.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.dao.AnalyticDao.MonthCashflow;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.viewmodel.CategoryDetailViewModel;
import com.epipasha.cashflow.data.viewmodel.ModelFactory;
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
    private int mCategoryId = DEFAULT_CATEGORY_ID;
    private CategoryDetailViewModel model;

    private EditText etTitle, etBudget;
    private RadioGroup rgType;
    private BarChart mChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = findViewById(R.id.category_detail_name);
        etBudget = findViewById(R.id.category_detail_budget);
        rgType = findViewById(R.id.type_group);
        mChart = findViewById(R.id.chart);

        Intent i = getIntent();
        if(i != null && i.hasExtra(EXTRA_CATEGORY_ID)){
            setTitle(getString(R.string.category));
            mCategoryId = i.getIntExtra(EXTRA_CATEGORY_ID, DEFAULT_CATEGORY_ID);
        }else
            setTitle(getString(R.string.new_category));

        model = ViewModelProviders.of(this, new ModelFactory(getApplication(), mCategoryId)).get(CategoryDetailViewModel.class);
        model.getCategory().observe(this, new Observer<Category>() {
            @Override
            public void onChanged(@Nullable Category category) {
                populateUI(category);
            }
        });
        model.getMonthCashflow().observe(this, new Observer<List<MonthCashflow>>() {
            @Override
            public void onChanged(@Nullable List<MonthCashflow> monthCashflows) {
                loadChart(monthCashflows);
            }
        });

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

    @Override
    public void saveObject(){
        String title = etTitle.getText().toString();

        if(title.isEmpty()){
            etTitle.setError(getString(R.string.error_fill_title));
            return;
        }

        int budget = 0;
        try {
            budget = Integer.valueOf(etBudget.getText().toString());
        }catch (Exception e){
            etBudget.setError(getString(R.string.error_fill_budget));
            return;
        }

        OperationType type = getSelectedType();

        if (type == null){
            return;
        }

        Category category = new Category(title, type, budget);

        model.saveObject(category);
        finish();

    }

    private void populateUI(Category category) {
        if (category == null) {
            return;
        }

        etTitle.setText(category.getTitle());
        etBudget.setText(String.valueOf(category.getBudget()));
        setCheckedType(category.getType());
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
