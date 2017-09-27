package com.epipasha.cashflow.fragments.summary;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.objects.OperationType;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class CardSummaryFragment extends Fragment {

    private OperationType type;

    private TextView lblType;
    private HorizontalBarChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null)
        type = (OperationType) args.getSerializable("type");

        View v = inflater.inflate(R.layout.fragment_card_summary, container, false);

        lblType = (TextView) v.findViewById(R.id.type);

        chart = (HorizontalBarChart) v.findViewById(R.id.chart);

        setData();

        return v;
    }

    private void setData() {

        GregorianCalendar c = new GregorianCalendar();
        Date end = new Date(c.getTimeInMillis());

        c.set(GregorianCalendar.DAY_OF_MONTH, c.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
        c.set(GregorianCalendar.HOUR_OF_DAY, 0);
        c.set(GregorianCalendar.MINUTE, 0);
        c.set(GregorianCalendar.SECOND, 0);
        c.set(GregorianCalendar.MILLISECOND, 0);
        Date start = new Date(c.getTimeInMillis());

        if (type == null)
            lblType.setText(getResources().getString(R.string.total));
        else if(type.equals(OperationType.IN))
            lblType.setText(getResources().getString(R.string.in));
        else if(type.equals(OperationType.OUT))
            lblType.setText(getResources().getString(R.string.out));

        int sum;
        int budget;
        if(type == null){
            // TODO Переделать на один запрос
            sum = CashFlowDbManager.getInstance(getActivity()).getTotalSum(OperationType.IN, start, end)
                    - CashFlowDbManager.getInstance(getActivity()).getTotalSum(OperationType.OUT, start, end);
            budget = CashFlowDbManager.getInstance(getActivity()).getTotalBudget(OperationType.IN)
                    - CashFlowDbManager.getInstance(getActivity()).getTotalBudget(OperationType.OUT);
        }else
        {
            sum = CashFlowDbManager.getInstance(getActivity()).getTotalSum(type, start, end);
            budget = CashFlowDbManager.getInstance(getActivity()).getTotalBudget(type);
        }

//        lblSum.setText(String.format("%,d",sum));
//        lblBudget.setText(String.format("%,d",budget));

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, budget));
        BarDataSet set = new BarDataSet(entries, getString(R.string.budget));
        set.setColor(ContextCompat.getColor(getActivity(), R.color.budget));
        set.setValueTextSize(10);

        List<BarEntry> entries1 = new ArrayList<>();
        entries1.add(new BarEntry(1f, sum));
        BarDataSet set1 = new BarDataSet(entries1, getString(R.string.fact));
        set1.setColor(ContextCompat.getColor(getActivity(), R.color.fact));
        set1.setValueTextSize(10);

        BarData data = new BarData();
        data.addDataSet(set);
        data.addDataSet(set1);

        final String[] labels = {getString(R.string.budget), getString(R.string.fact)};

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

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

        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh

    }

}
