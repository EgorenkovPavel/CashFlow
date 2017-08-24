package com.epipasha.cashflow.fragments.summary;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowDbHelper;
import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.objects.Account;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CardAccountFragment extends Fragment {

    private HorizontalBarChart chart;

    SimpleCursorAdapter mAdapter;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_account, container, false);

        chart = (HorizontalBarChart)v.findViewById(R.id.chart);
        list = (ListView)v.findViewById(R.id.list);

        setData();
        //initList();

        return v;
    }

    private void setData() {

        final ArrayList<Account> accounts = CashFlowDbManager.getInstance(getActivity()).getAccounts();

        List<BarEntry> entries = new ArrayList<>();
        String[] labels = new String[accounts.size()];
        for (int i = 0; i<accounts.size(); i++){
            entries.add(new BarEntry(i, accounts.get(i).getBalance()));
            labels[i] = accounts.get(i).getName();
        }

        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setValueTextSize(10);

        BarData data = new BarData();
        data.addDataSet(set);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextSize(10);

        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setEnabled(false);

        YAxis rigth = chart.getAxisRight();
        rigth.setAxisMinimum(0f);
        rigth.setEnabled(false);

        chart.getLegend().setEnabled(false);

        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);

        chart.setMinimumHeight(accounts.size() * 100);

        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh

    }

    private void initList(){
/*
        List accounts = CashFlowDbManager.getInstance(getActivity()).getAccounts();

        String stAccount = getString(R.str)

        List<Map<String, String>> data = new ArrayList<>();

        String[] from = new String[]{};
        int[] to = new int[]{R.id.account, R.id.sum};

        list.setAdapter(new SimpleAdapter(getActivity(), data, R.layout.list_item_account_summary, from, to)
  */  }

}
