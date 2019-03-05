package com.epipasha.cashflow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.views.Indicator;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewGroup main = findViewById(R.id.includeMain);
        ViewGroup in = findViewById(R.id.includeIn);
        ViewGroup out = findViewById(R.id.includeOut);

        TextView tvMainTitle = main.findViewById(R.id.tvTitle);
        TextView tvMainSum = main.findViewById(R.id.tvSum);
        Indicator mainIndicator = main.findViewById(R.id.indicator);

        TextView tvInTitle = in.findViewById(R.id.tvTitle);
        TextView tvInSum = in.findViewById(R.id.tvSum);
        Indicator inIndicator = in.findViewById(R.id.indicator);

        TextView tvOutTitle = out.findViewById(R.id.tvTitle);
        TextView tvOutSum = out.findViewById(R.id.tvSum);
        Indicator outIndicator = out.findViewById(R.id.indicator);

        tvMainTitle.setText(getString(R.string.total_sum));
        tvInTitle.setText(getString(R.string.in));
        tvOutTitle.setText(getString(R.string.out));

        float inBudget = 80000;
        float outBudget = 80000;
        float inCashflow = 45000;
        float outCashflow = 40000;

        tvMainSum.setText(String.valueOf(inCashflow - inBudget + outBudget - outCashflow));
        tvInSum.setText(String.valueOf(inCashflow - inBudget));
        tvOutSum.setText(String.valueOf(outBudget - outCashflow));

        initIndicator(mainIndicator);
        mainIndicator.setBudget(inBudget - outBudget);
        mainIndicator.setCashflow(inCashflow - outCashflow);
        mainIndicator.setSuccessColor(Color.RED);
        mainIndicator.setErrorColor(Color.GREEN);

        initIndicator(inIndicator);
        inIndicator.setBudget(inBudget);
        inIndicator.setCashflow(inCashflow);
        inIndicator.setSuccessColor(Color.RED);
        inIndicator.setErrorColor(Color.GREEN);

        initIndicator(outIndicator);
        outIndicator.setBudget(outBudget);
        outIndicator.setCashflow(outCashflow);
    }

    private void initIndicator(Indicator indicator){
        Calendar cal = Calendar.getInstance();
        indicator.setMin(1);
        indicator.setMax(cal.getMaximum(Calendar.DAY_OF_MONTH));
        indicator.setValue(cal.get(Calendar.DAY_OF_MONTH));
    }
}
