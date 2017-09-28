package com.epipasha.cashflow.fragments.summary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.objects.Account;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class CardAccountFragment extends Fragment {

    private HorizontalBarChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_account, container, false);

        chart = (HorizontalBarChart)v.findViewById(R.id.chart);

        setData();

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

        YAxis right = chart.getAxisRight();
        right.setAxisMinimum(0f);
        right.setEnabled(false);

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

}
