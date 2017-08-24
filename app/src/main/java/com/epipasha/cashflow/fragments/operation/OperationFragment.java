package com.epipasha.cashflow.fragments.operation;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.fragments.ListDetailActivity;
import com.epipasha.cashflow.fragments.ListFragment;
import com.epipasha.cashflow.objects.Operation;

import java.util.ArrayList;

/**
 * Created by Pavel on 08.11.2016.
 */

public class OperationFragment extends ListFragment<Operation> {

    @Override
    public void addInstance() {
        Intent i = new Intent();
        i.putExtra("Instance", new Operation());
        i.putExtra("Position", -1);
        i.setClass(getActivity(), ListDetailActivity.class);
        startActivityForResult(i, getActivity().RESULT_CANCELED);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(ListFragment fragment, ArrayList<Operation> list) {
        return new OperationListAdapter(fragment, list);
    }

    @Override
    protected ArrayList<Operation> getList() {
        return CashFlowDbManager.getInstance(getActivity()).getOperations();
    }
}
