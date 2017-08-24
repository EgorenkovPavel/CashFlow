package com.epipasha.cashflow.fragments.summary;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.objects.OperationType;

public class SummaryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction t = fm.beginTransaction();

        CardAccountFragment accFrag = new CardAccountFragment();
        CardSummaryFragment totalFrag = new CardSummaryFragment();
        CardSummaryFragment inFrag = new CardSummaryFragment();
        CardSummaryFragment outFrag = new CardSummaryFragment();

        Bundle argsIn = new Bundle();
        argsIn.putSerializable("type", OperationType.IN);
        inFrag.setArguments(argsIn);

        Bundle argsOut = new Bundle();
        argsOut.putSerializable("type", OperationType.OUT);
        outFrag.setArguments(argsOut);

        t.add(R.id.container, accFrag);
        t.add(R.id.container, totalFrag);
        t.add(R.id.container, inFrag);
        t.add(R.id.container, outFrag);
        t.commit();

        return v;
    }
}
