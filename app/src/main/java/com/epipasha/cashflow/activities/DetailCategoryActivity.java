package com.epipasha.cashflow.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
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

import java.util.ArrayList;
import java.util.List;

public class DetailCategoryActivity extends DetailActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_DETAIL_LOADER = 353;
    private static final int ID_CHART_LOADER = 789;

    private Uri mUri;
    private EditText etTitle, etBudget;
    private RadioGroup rgType;
    private boolean isNew;
    private BarChart mChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText)findViewById(R.id.category_detail_name);
        etBudget = (EditText)findViewById(R.id.category_detail_budget);
        rgType = (RadioGroup)findViewById(R.id.type_group);
        mChart = (BarChart)findViewById(R.id.chart);

        mUri = getIntent().getData();
        isNew = mUri == null;

        if (!isNew) {
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
            getSupportLoaderManager().initLoader(ID_CHART_LOADER, null, this);
        }

        if(isNew) {
            setTitle(getString(R.string.new_category));
        }else{
            setTitle(getString(R.string.category));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        null,
                        null,
                        null,
                        null);

            case ID_CHART_LOADER:

                return new CursorLoader(this,
                        CashFlowContract.CategoryCostEntry.buildCategoryCostUriWithId(Long.valueOf(mUri.getLastPathSegment())),
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()){
            case ID_DETAIL_LOADER:{
                if(data != null && data.moveToFirst()){
                    etTitle.setText(data.getString(data.getColumnIndex(CategoryEntry.COLUMN_TITLE)));
                    etBudget.setText(String.valueOf(data.getInt(data.getColumnIndex(CategoryEntry.COLUMN_BUDGET))));
                    setCheckedType(OperationType.toEnum(data.getInt(data.getColumnIndex(CategoryEntry.COLUMN_TYPE))));
                }
                break;
            }
            case ID_CHART_LOADER:{
                if(data != null && data.moveToFirst()){
                    loadChart(data);
                }

                break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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

    private void loadChart(Cursor cursor){

        List<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        int column = 0;
        do {
            entries.add(new BarEntry(column, cursor.getInt(2)));

            int month = cursor.getInt(1) + 1;
            int year = cursor.getInt(0);
            labels.add("" + month + ", " + year);

            column++;
        }while(cursor.moveToNext());

        BarDataSet set = new BarDataSet(entries, "BarDataSet");

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        data.setValueTextSize(10);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value - (int)(value) == 0){
                    return labels.get((int)value);
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
                mChart.getAxisLeft().getLimitLines().clear();

                if (!etBudget.getText().toString().isEmpty()) {
                    YAxis yAxis = mChart.getAxisLeft();
                    LimitLine line = new LimitLine(Float.valueOf(etBudget.getText().toString()));
                    line.setEnabled(true);
                    line.setLineWidth(3);
                    yAxis.addLimitLine(line);
                }

            }
        });

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        if (!etBudget.getText().toString().isEmpty()) {
            LimitLine line = new LimitLine(Float.valueOf(etBudget.getText().toString()));
            line.setEnabled(true);
            line.setLineWidth(3);
            yAxis.addLimitLine(line);
        }

        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        mChart.setData(data);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.setVisibleXRangeMaximum(3);
        mChart.moveViewToX(column);
        mChart.invalidate(); // refresh
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

        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_TITLE, title);
        values.put(CategoryEntry.COLUMN_TYPE, type.toDbValue());
        values.put(CategoryEntry.COLUMN_BUDGET, budget);

        if (isNew){
            mUri = getContentResolver().insert(CategoryEntry.CONTENT_URI, values);
            isNew = false;
        } else {
            getContentResolver().update(mUri, values, null, null);
        }

        finish();
    }
}
