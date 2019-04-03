package com.epipasha.cashflow.fragments.home;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.complex.CategoryWithCashflow;
import com.epipasha.cashflow.data.objects.OperationType;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private TextView tvTotalSum;
    private TextView tvCashflow;
    private TextView tvIn;
    private TextView tvOut;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        tvTotalSum = view.findViewById(R.id.tvTotalSum);
        tvCashflow = view.findViewById(R.id.tvCashflow);
        tvIn = view.findViewById(R.id.tvIn);
        tvOut = view.findViewById(R.id.tvOut);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity().getApplication())).get(HomeViewModel.class);

        mViewModel.getAccounts().observe(this, accounts -> {
            int sum = 0;
            for (AccountWithBalance account:accounts){
                sum += account.getSum();
            }
            tvTotalSum.setText(String.format(Locale.getDefault(), "%,d", sum));
        });

        mViewModel.getCategories().observe(this, categories -> {
            int inBudget = 0;
            int inFact = 0;
            int outBudget = 0;
            int outFact = 0;

            for (CategoryWithCashflow category: categories) {

                OperationType type = category.getType();
                switch (type){
                    case IN:{
                        inBudget += category.getBudget();
                        inFact += category.getCashflow();
                        break;
                    }
                    case OUT:{
                        outBudget += category.getBudget();
                        outFact += category.getCashflow();
                        break;
                    }
                }
            }

            tvIn.setText(String.format(Locale.getDefault(), "%,d / %,d", inFact, inBudget));
            tvOut.setText(String.format(Locale.getDefault(), "%,d / %,d", outFact, outBudget));
            tvCashflow.setText(String.format(Locale.getDefault(), "%,d", inFact - outFact));
        });

    }

}
