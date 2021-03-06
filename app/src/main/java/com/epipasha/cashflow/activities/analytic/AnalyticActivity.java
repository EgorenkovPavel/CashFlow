package com.epipasha.cashflow.activities.analytic;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.epipasha.cashflow.Prefs;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.data.dao.AnalyticDao.CategoryCashflow;
import com.epipasha.cashflow.data.dao.AnalyticDao.MonthCashflow;
import com.epipasha.cashflow.data.objects.OperationType;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyticActivity extends BaseActivity {

    private LineChart mChartIn;
    private PieChart mChartPie;

    private AnalyticViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChartIn = findViewById(R.id.chart_in);
        mChartPie = findViewById(R.id.chart_pie);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication()))
                .get(AnalyticViewModel.class);

        model.getMonthCashflow().observe(this, this::loadChart);

        model.getMonthOutCashflow().observe(this, this::loadPie);
    }

    private void loadPie(List<CategoryCashflow> categoryCashflow) {

        List<PieEntry> entries = new ArrayList<>();

        for(CategoryCashflow cashflow:categoryCashflow){
            entries.add(new PieEntry(cashflow.getSum(), cashflow.getTitle()));
        }

        PieDataSet set = new PieDataSet(entries, "Election Results");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(set);
        mChartPie.setEntryLabelColor(Color.BLACK);
        mChartPie.getLegend().setEnabled(false);
        mChartPie.getDescription().setEnabled(false);
        mChartPie.setData(data);
        mChartPie.invalidate(); // refresh

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.analytic_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                final CharSequence[] titles = new CharSequence[6];
                titles[0] = "IN";
                titles[1] = "OUT";
                titles[2] = "IN budget";
                titles[3] = "OUT budget";
                titles[4] = "Delta";
                titles[5] = "Cashflow";

                final boolean[] values = new boolean[6];
                values[0] = Prefs.AnalyticChartPrefs.showInGraphic(this);
                values[1] = Prefs.AnalyticChartPrefs.showOutGraphic(this);
                values[2] = Prefs.AnalyticChartPrefs.showInBudgetGraphic(this);
                values[3] = Prefs.AnalyticChartPrefs.showOutBudgetGraphic(this);
                values[4] = Prefs.AnalyticChartPrefs.showDeltaGraphic(this);
                values[5] = Prefs.AnalyticChartPrefs.showCashflowGraphic(this);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose graphics")
                        .setMultiChoiceItems(titles, values, (dialogInterface, i, b) -> values[i] = b)
                        .setPositiveButton(R.string.ok, (dialog, id) -> {
                            Prefs.AnalyticChartPrefs.saveShowInGraphic(AnalyticActivity.this, values[0]);
                            Prefs.AnalyticChartPrefs.saveShowOutGraphic(AnalyticActivity.this, values[1]);
                            Prefs.AnalyticChartPrefs.saveShowInBudgetGraphic(AnalyticActivity.this, values[2]);
                            Prefs.AnalyticChartPrefs.saveShowOutBudgetGraphic(AnalyticActivity.this, values[3]);
                            Prefs.AnalyticChartPrefs.saveShowDeltaGraphic(AnalyticActivity.this, values[4]);
                            Prefs.AnalyticChartPrefs.saveShowCashflowGraphic(AnalyticActivity.this, values[5]);

                            YAxis yAxis = mChartIn.getAxisLeft();
                            yAxis.getLimitLines().clear();

                            // todo restart loading chart
                            //startLoadingChart();
                        })
                        .setNegativeButton(R.string.cancel, (dialog, id) -> {
                        });

                builder.create().show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getColor(OperationType type){
        switch (type){
            case IN:
                return getResources().getColor(R.color.colorPrimary);
            case OUT:
                return getResources().getColor(R.color.colorAccent);
            default:
                return getResources().getColor(android.R.color.white);
        }
    }

    private void loadChart(List<MonthCashflow> list){

        DateFormat df = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        //todo rewrite
        Map<String, ChartData> map = new HashMap<>();
        for (MonthCashflow monthCashflow:list) {
            cal.set(monthCashflow.getYear(), monthCashflow.getMonth(), 1);
            String key = df.format(cal.getTime());

            ChartData chartData = map.get(key);
            if (chartData == null){
                chartData = new ChartData();
            }

            switch (monthCashflow.getType()){
                case IN:{
                    chartData.setIn(monthCashflow.getSum());
                    break;
                }
                case OUT:{
                    chartData.setOut(monthCashflow.getSum());
                    break;
                }
                default:
                    continue;
            }

            map.put(key, chartData);
        }


        List<Entry> entriesIn = new ArrayList<>();
        List<Entry> entriesOut = new ArrayList<>();
        List<Entry> entriesDelta = new ArrayList<>();
        List<Entry> entriesCash = new ArrayList<>();

        final List<String> labels = new ArrayList<>();
        int column = 0;
        int cash = 0;

        for(Map.Entry<String, ChartData> entry : map.entrySet()) {

            ChartData chartData = entry.getValue();

            int in  = chartData.getIn();
            int out  = chartData.getOut();
            cash += (in - out);

            entriesIn.add(new Entry(column, in));
            entriesOut.add(new Entry(column, out));
            entriesDelta.add(new Entry(column, in - out));
            entriesCash.add(new Entry(column, cash));

            labels.add(entry.getKey());

            column++;
        }

        LineDataSet setIn = new LineDataSet(entriesIn, getString(R.string.in));
        LineDataSet setOut = new LineDataSet(entriesOut, getString(R.string.out));
        LineDataSet setDelta = new LineDataSet(entriesDelta, "Delta");
        LineDataSet setCash = new LineDataSet(entriesCash, getString(R.string.cashflow));

        setIn.setColor(getColor(OperationType.IN));
        setOut.setColor(getColor(OperationType.OUT));

        setIn.setLineWidth(3);
        setOut.setLineWidth(3);
        setDelta.setLineWidth(3);
        setCash.setLineWidth(3);

        setIn.setCircleRadius(1f);
        setOut.setCircleRadius(1f);
        setDelta.setCircleRadius(1f);
        setCash.setCircleRadius(1f);

        List<ILineDataSet> set = new ArrayList<>();
        if (Prefs.AnalyticChartPrefs.showInGraphic(this)) set.add(setIn);
        if (Prefs.AnalyticChartPrefs.showOutGraphic(this)) set.add(setOut);
        if (Prefs.AnalyticChartPrefs.showDeltaGraphic(this)) set.add(setDelta);
        if (Prefs.AnalyticChartPrefs.showCashflowGraphic(this)) set.add(setCash);

        LineData data = new LineData(set);
        //data.setBarWidth(0.9f); // set custom bar width
        data.setValueTextSize(10);
        data.setHighlightEnabled(false);

        XAxis xAxis = mChartIn.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter((value, axis) -> {
            if(value - (int)(value) == 0 && value > 0){
                return labels.get((int)value - 1);
            }else{
                return "";
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

    class ChartData{

        private int in;
        private int out;

        public int getIn() {
            return in;
        }

        public void setIn(int in) {
            this.in = in;
        }

        public int getOut() {
            return out;
        }

        public void setOut(int out) {
            this.out = out;
        }
    }

}
