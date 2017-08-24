package com.epipasha.cashflow.fragments.goal;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.fragments.ListDetailActivity;
import com.epipasha.cashflow.fragments.ListFragment;
import com.epipasha.cashflow.fragments.account.AccountListAdapter;
import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Goal;

import java.io.Serializable;
import java.util.ArrayList;

public class GoalFragment extends ListFragment<Goal> {

    @Override
    public void addInstance() {
        Intent i = new Intent();
        i.setClass(getActivity(), ListDetailActivity.class);
        i.putExtra("Instance", new Goal());
        i.putExtra("Position", -1);
        startActivityForResult(i, getActivity().RESULT_CANCELED);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(ListFragment fragment, ArrayList<Goal> list) {
        return new GoalListAdapter(fragment, list);
    }

    @Override
    protected ArrayList<Goal> getList() {
        return CashFlowDbManager.getInstance(getActivity()).getGoals();
    }

}
