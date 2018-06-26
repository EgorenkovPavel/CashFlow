package com.epipasha.cashflow;

import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.objects.OperationType;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AnalyticActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_CHART_LOADER = 789;
    private LineChart mChartIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChartIn = findViewById(R.id.chart_in);

        getSupportLoaderManager().initLoader(ID_CHART_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this,
                CashFlowContract.CategoryCostEntry.buildCategoryCostUriWithOperationType(),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            loadChart(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void loadChart(Cursor cursor){

        List<Entry> entriesIn = new ArrayList<>();
        List<Entry> entriesOut = new ArrayList<>();
        List<Entry> entriesDelta = new ArrayList<>();
        List<Entry> entriesCash = new ArrayList<>();

        final List<String> labels = new ArrayList<>();
        int column = 0;
        int cash = 0;

        do {
            int in  = cursor.getInt(2);
            int out  = cursor.getInt(3);
            cash += (in - out);

            entriesIn.add(new Entry(column, in));
            entriesOut.add(new Entry(column, out));
            entriesDelta.add(new Entry(column, in - out));
            entriesCash.add(new Entry(column, cash));

            int month = cursor.getInt(1);
            int year = cursor.getInt(0);

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);

            DateFormat df = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

            labels.add(df.format(cal.getTime()));

            column++;
        }while(cursor.moveToNext());

        LineDataSet setIn = new LineDataSet(entriesIn, getString(R.string.in));
        LineDataSet setOut = new LineDataSet(entriesOut, getString(R.string.out));
        LineDataSet setDelta = new LineDataSet(entriesDelta, "Delta");
        LineDataSet setCash = new LineDataSet(entriesCash, getString(R.string.cashflow));

        setIn.setColor(getResources().getColor(R.color.colorPrimary));
        setOut.setColor(getResources().getColor(R.color.colorAccent));

        setIn.setLineWidth(3);
        setOut.setLineWidth(3);
        setDelta.setLineWidth(3);
        setCash.setLineWidth(3);

        setIn.setCircleRadius(1f);
        setOut.setCircleRadius(1f);
        setDelta.setCircleRadius(1f);
        setCash.setCircleRadius(1f);

        List<ILineDataSet> set = new ArrayList<ILineDataSet>();
        set.add(setIn);
        set.add(setOut);
        set.add(setDelta);
        set.add(setCash);

        LineData data = new LineData(set);
        //data.setBarWidth(0.9f); // set custom bar width
        data.setValueTextSize(10);
        data.setHighlightEnabled(false);

        XAxis xAxis = mChartIn.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
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

        YAxis yAxis = mChartIn.getAxisLeft();
        //yAxis.setAxisMinimum(0);
        //yAxis.setEnabled(false);
        //addBudgetLineToChart();

        mChartIn.getAxisRight().setEnabled(false);
        Description desc = mChartIn.getDescription();
        desc.setText("Month analytic");

        //.setEnabled(false);
        //mChartIn.getLegend().setEnabled(false);

        mChartIn.setData(data);
        //mChartIn.setFitBars(true); // make the x-axis fit exactly all bars
        mChartIn.setVisibleXRangeMaximum(3);
        mChartIn.moveViewToX(column);
        mChartIn.invalidate(); // refresh
    }
}
