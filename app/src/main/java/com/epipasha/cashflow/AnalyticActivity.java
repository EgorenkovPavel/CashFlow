package com.epipasha.cashflow;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.dao.AnalyticDao.MonthCashflow;
import com.epipasha.cashflow.objects.OperationType;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyticActivity extends BaseActivity {

    private AppDatabase mDb;

    private LineChart mChartIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChartIn = findViewById(R.id.chart_in);

        mDb = AppDatabase.getInstance(getApplicationContext());
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                LiveData<List<MonthCashflow>> list = mDb.analyticDao().loadAllMonthCashflow();
                list.observe(AnalyticActivity.this, new Observer<List<MonthCashflow>>() {
                    @Override
                    public void onChanged(@Nullable List<MonthCashflow> monthCashflows) {
                        loadChart(monthCashflows);
                    }
                });
            }
        });
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
                values[0] = Prefs.AnalyticChartPrefs.showInGrafic(this);
                values[1] = Prefs.AnalyticChartPrefs.showOutGrafic(this);
                values[2] = Prefs.AnalyticChartPrefs.showInBudgetGrafic(this);
                values[3] = Prefs.AnalyticChartPrefs.showOutBudgetGrafic(this);
                values[4] = Prefs.AnalyticChartPrefs.showDeltaGrafic(this);
                values[5] = Prefs.AnalyticChartPrefs.showCashflowGrafic(this);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose grafics")
                        .setMultiChoiceItems(titles, values, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                values[i] = b;
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Prefs.AnalyticChartPrefs.saveShowInGrafic(AnalyticActivity.this, values[0]);
                                Prefs.AnalyticChartPrefs.saveShowOutGrafic(AnalyticActivity.this, values[1]);
                                Prefs.AnalyticChartPrefs.saveShowInBudgetGrafic(AnalyticActivity.this, values[2]);
                                Prefs.AnalyticChartPrefs.saveShowOutBudgetGrafic(AnalyticActivity.this, values[3]);
                                Prefs.AnalyticChartPrefs.saveShowDeltaGrafic(AnalyticActivity.this, values[4]);
                                Prefs.AnalyticChartPrefs.saveShowCashflowGrafic(AnalyticActivity.this, values[5]);

                                YAxis yAxis = mChartIn.getAxisLeft();
                                yAxis.getLimitLines().clear();

                                // todo restart loading chart
                                //startLoadingChart();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                builder.create().show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addBudgetLines(Cursor cursor) {
        YAxis yAxis = mChartIn.getAxisLeft();
        do{
            OperationType type = OperationType.toEnum(cursor.getInt(0));
            if(type == OperationType.IN && !Prefs.AnalyticChartPrefs.showInBudgetGrafic(this))
                continue;
            else if (type == OperationType.OUT && !Prefs.AnalyticChartPrefs.showOutBudgetGrafic(this))
                continue;

            LimitLine line = new LimitLine(cursor.getInt(1));
            line.setEnabled(true);
            line.setLineWidth(1);
            line.setLineColor(getColor(type));
            line.setTextColor(getColor(type));
            line.setLabel(getString(R.string.budget) + " " + type.getTitle(this));
            yAxis.addLimitLine(line);
        }while(cursor.moveToNext());

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
        if (Prefs.AnalyticChartPrefs.showInGrafic(this)) set.add(setIn);
        if (Prefs.AnalyticChartPrefs.showOutGrafic(this)) set.add(setOut);
        if (Prefs.AnalyticChartPrefs.showDeltaGrafic(this)) set.add(setDelta);
        if (Prefs.AnalyticChartPrefs.showCashflowGrafic(this)) set.add(setCash);

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
                if(value - (int)(value) == 0 && value > 0){
                    return labels.get((int)value - 1);
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
